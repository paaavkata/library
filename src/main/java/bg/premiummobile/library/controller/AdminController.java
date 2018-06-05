package bg.premiummobile.library.controller;

import java.util.List;

import javax.naming.SizeLimitExceededException;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import bg.premiummobile.library.data.BookRepository;
import bg.premiummobile.library.data.GenreRepository;
import bg.premiummobile.library.dto.BookDTO;
import bg.premiummobile.library.dto.ErrorDTO;
import bg.premiummobile.library.exception.EntityNotFoundException;
import bg.premiummobile.library.exception.ImageSaveException;
import bg.premiummobile.library.exception.UserNotAuthorizedException;
import bg.premiummobile.library.model.Book;
import bg.premiummobile.library.model.Genre;
import bg.premiummobile.library.service.FileService;
import bg.premiummobile.library.service.UserService;
import bg.premiummobile.library.util.UserLevel;

@RestController
@RequestMapping("/admin")
public class AdminController {

	private BookRepository bookRepository;
	
	private GenreRepository genreRepository;
	
	private UserService userService;
	
	private FileService fileService;
	
	private List<Genre> genres;
	
	@Value("#{'${genres}'.split(',')}")
	private List<String> configGenres;
	
	@Value("${bookImages.maxSize:5}")
	private int maxImageSize;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	public AdminController(BookRepository bookRepository, GenreRepository genreRepository, UserService userService, FileService fileService){
		this.bookRepository = bookRepository;
		this.genreRepository = genreRepository;
		this.userService = userService;
		this.fileService = fileService;
		this.genres = genreRepository.findAll();
	}
	
	@PutMapping("/book/{id}")
	public Book updateBook(@PathVariable(value = "id") Long bookId, @Valid @RequestBody Book book, @RequestHeader String token, @RequestHeader long userId) {
		if(userService.hasAccess(token, userId, UserLevel.MODERATOR)){
			if(checkGenre(book.getGenre())){
				Book oldBook = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("Book", "id", String.valueOf(bookId)));
				oldBook.setAuthor(book.getAuthor());
				oldBook.setGenre(book.getGenre());
				oldBook.setImage(book.getImage());
				oldBook.setName(book.getName());
				oldBook.setPages(book.getPages());
				oldBook.setResume(book.getResume());
				logger.info("Book with id " + oldBook.getId() + " updated.");
				return bookRepository.save(oldBook);
			}
			else{
				throw new EntityNotFoundException("Genre", "name", book.getGenre());
			}
		}
		else{
			throw new UserNotAuthorizedException();
		}	
	}
	
	@DeleteMapping("/book/{id}")
	public boolean deleteBook(@PathVariable(value = "id") Long bookId, @RequestHeader String token, @RequestHeader long userId) {
		if(userService.hasAccess(token, userId, UserLevel.MODERATOR)){
			Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("Book", "id", String.valueOf(bookId)));
	
			bookRepository.delete(book);
	
		    return true;
		}
		else{
			throw new UserNotAuthorizedException();
		}
	}
	
	
	@PostMapping("/genre/new/{genre}")
	public boolean addGenre(@PathVariable String genre, 
			@RequestHeader String token, @RequestHeader long userId) {
		if(userService.hasAccess(token, userId, UserLevel.MODERATOR)){
			if(!checkGenre(genre)){
				Genre g = genreRepository.save(new Genre(genre));
				if(null != g){
					genres.add(g);
					logger.info("New genre with name " + g.getName() + " created.");
					return true;
				}
			}
			return false;
		}
		else{
			throw new UserNotAuthorizedException();
		}
	}
	
	@PostMapping("/book")
	public Book createBook(@ModelAttribute BookDTO dto, @RequestParam("image") MultipartFile image,@RequestHeader String token, @RequestHeader long userId) throws ImageSaveException, SizeLimitExceededException {
		long fileSize = image.getSize();
		if(fileSize > 1024 * 1024 * maxImageSize){
			throw new SizeLimitExceededException("Image file size exceeds max accepted size of " + maxImageSize + " MB");
		}
		if(userService.hasAccess(token, userId, UserLevel.MODERATOR)){
			if(checkGenre(dto.getGenre())){
				Book book = new Book(dto);
				String filePath = fileService.persistFile(image, dto.getName());
				book.setImage(filePath);
				logger.info("New book with name " + book.getName() + " created.");
				return bookRepository.save(book);
			}
			else{
				throw new EntityNotFoundException("Genre", "name", dto.getGenre());
			}
		}
		else{
			throw new UserNotAuthorizedException();
		}
	}
	
	@ExceptionHandler(UserNotAuthorizedException.class)
	@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ErrorDTO handleAuthorizationException(Exception e) {
        return new ErrorDTO(401, e.getMessage());
    }
	
	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorDTO handleNotFoundException(Exception e) {
        return new ErrorDTO(404, e.getMessage());
    }
	
	@ExceptionHandler(ImageSaveException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO handleImageNotSavedException(Exception e) {
        return new ErrorDTO(500, e.getMessage());
    }
	
	@ExceptionHandler(SizeLimitExceededException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO handleSizeLimitExceededException(Exception e) {
        return new ErrorDTO(500, e.getMessage());
    }
	private boolean checkGenre(String genre) {
		if(genres.isEmpty()){
			loadGenresInDatabase();
		}
		for(Genre g : genres){
			if(g.getName().equals(genre)){
				return true;
			}
		}
		return false;
	}

	private void loadGenresInDatabase() {
		for(String s : configGenres){
			genreRepository.save(new Genre(s));
		}
	}
}
