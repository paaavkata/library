package bg.premiummobile.library.exception;

public class EntityNotFoundException extends RuntimeException{
	
	public EntityNotFoundException(String entityName, String fieldName, String fieldValue) {
        super(String.format("%s not found with %s : '%s'", entityName, fieldName, fieldValue));
    }
}
