package bg.premiummobile.library.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import bg.premiummobile.library.data.BookRepository;
import bg.premiummobile.library.data.GenreRepository;
import bg.premiummobile.library.dto.ErrorDTO;
import bg.premiummobile.library.exception.EntityNotFoundException;
import bg.premiummobile.library.exception.UserNotAuthorizedException;
import bg.premiummobile.library.model.Book;
import bg.premiummobile.library.model.Genre;
import bg.premiummobile.library.service.UserService;
import bg.premiummobile.library.util.UserLevel;

@RestController
@RequestMapping("/books")
public class BookController {
	
	private BookRepository bookRepository;
	
	private GenreRepository genreRepository;
	
	private UserService userService;
	
	private List<Genre> genres;
	
	@Autowired
	public BookController(BookRepository bookRepository, GenreRepository genreRepository, UserService userService){
		this.bookRepository = bookRepository;
		this.userService = userService;
		this.genres = genreRepository.findAll();
	}
	
	@GetMapping("/all")
	public Page<Book> getAllBooks(@RequestParam(value = "page") String page, @RequestParam(value = "size") String size, 
			@RequestHeader String token, @RequestHeader long userId) {
		if(userService.hasAccess(token, userId, UserLevel.USER)){
			return bookRepository.findAll(PageRequest.of(Integer.valueOf(page), Integer.valueOf(size)));
		}
		else{
			throw new UserNotAuthorizedException();
		}
	}
	
	@GetMapping("/genre/{genre}")
	public Page<Book> getAllBooksByGenre(@PathVariable(value = "genre") String genre, 
			@RequestParam(value = "page") String page, @RequestParam(value = "size") String size, @RequestHeader String token, @RequestHeader long userId) {
		if(userService.hasAccess(token, userId, UserLevel.USER)){
			if(checkGenre(genre)){
				return bookRepository.findByGenre(genre, PageRequest.of(Integer.valueOf(page), Integer.valueOf(size)));
			}
			else{
				throw new EntityNotFoundException("Genre", "name", genre);
			}
		}
		else{
			throw new UserNotAuthorizedException();
		}
	}
	
	@GetMapping("/name/{name}")
	public Page<Book> getAllBooksByName(@PathVariable(value = "name") String name, 
			@RequestParam(value = "page") String page, @RequestParam(value = "size") String size, @RequestHeader String token, @RequestHeader long userId) {
		if(userService.hasAccess(token, userId, UserLevel.USER)){
			return bookRepository.findByNameIgnoreCaseContaining(name, PageRequest.of(Integer.valueOf(page), Integer.valueOf(size)));
		}
		else{
			throw new UserNotAuthorizedException();
		}
	}
	
	@GetMapping("/genre/all")
	public List<Genre> getAllGenres(@RequestHeader String token, @RequestHeader long userId) {
		if(userService.hasAccess(token, userId, UserLevel.USER)){
			return genres;
		}
		else{
			throw new UserNotAuthorizedException();
		}
	}

	@GetMapping("/{id}")
	public Book getBookById(@PathVariable(value = "id") Long bookId, @RequestHeader String token, @RequestHeader long userId) {
		if(userService.hasAccess(token, userId, UserLevel.USER)){
			return bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("Book", "id", String.valueOf(bookId)));
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
	
	private boolean checkGenre(String genre) {
		for(Genre g : genres){
			if(g.getName().equals(genre)){
				return true;
			}
		}
		return false;
	}
}
