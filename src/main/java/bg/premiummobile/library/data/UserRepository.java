package bg.premiummobile.library.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bg.premiummobile.library.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	
}
