package rentit.com.common.exceptions;

public class ExtensionNotFound extends Exception{
	private static final long serialVersionUID = -9096204555461030297L;
	
	public ExtensionNotFound(Long oid, Long eid) {
        super(String.format("Extension not found for purchase order %d! (Extension id: %d)", oid, eid));
    }
}
