package bg.premiummobile.library.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bg.premiummobile.library.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>{

	public Page<Book> findByGenre(String genre, Pageable pageable);
	
	public Page<Book> findByNameIgnoreCaseContaining(String name, Pageable pageable);
	
	public Page<Book> findAll(Pageable pageable);

}
