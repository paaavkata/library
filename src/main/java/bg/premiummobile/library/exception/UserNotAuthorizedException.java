package bg.premiummobile.library.exception;

public class UserNotAuthorizedException extends RuntimeException {

	public UserNotAuthorizedException(){
		super("User not authorized.");
	}
}
