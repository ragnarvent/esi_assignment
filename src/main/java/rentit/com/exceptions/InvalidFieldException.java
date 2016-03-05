package rentit.com.exceptions;

public class InvalidFieldException extends Exception{
	private static final long serialVersionUID = 2917519943203547297L;

	public InvalidFieldException(String message) {
        super(message);
    }
}
