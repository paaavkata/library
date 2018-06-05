package bg.premiummobile.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class WrongPasswordException extends RuntimeException {

	public WrongPasswordException(){
		super("Wrong username or password.");
	}
}
