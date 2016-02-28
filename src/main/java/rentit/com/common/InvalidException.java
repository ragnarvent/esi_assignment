package rentit.com.common;

public class InvalidException extends Exception {
	private static final long serialVersionUID = 3088559746512334236L;

	public InvalidException() {
		super();
	}

	public InvalidException(String message) {
		super(message);
	}

	public InvalidException(String message, int productId, Throwable cause) {
		super(message, cause);
	}

}
