package rentit.com.common;

public class RentitException extends Exception {
	private static final long serialVersionUID = 3088559746512334236L;

	public RentitException() {
		super();
	}

	public RentitException(String message) {
		super(message);
	}

	public RentitException(String message, Throwable cause) {
		super(message, cause);
	}

}
