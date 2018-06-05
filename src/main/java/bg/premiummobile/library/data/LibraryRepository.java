package bg.premiummobile.library.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bg.premiummobile.library.model.Library;

@Repository
public interface LibraryRepository extends JpaRepository<Library, Long>{

}
