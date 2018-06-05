package bg.premiummobile.library.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bg.premiummobile.library.model.Genre;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long>{
	
}
