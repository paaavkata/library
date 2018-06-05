package bg.premiummobile.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class UsernameAlreadyTakenException extends RuntimeException {

	public UsernameAlreadyTakenException(){
		super("This username is already taken, please use another.");
	}
}
