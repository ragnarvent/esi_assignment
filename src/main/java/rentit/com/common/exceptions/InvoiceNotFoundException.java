package rentit.com.common.exceptions;

public class InvoiceNotFoundException extends Exception{
	private static final long serialVersionUID = 734620023578822237L;
	
	public InvoiceNotFoundException(Long id) {
        super(String.format("Invoice with id %d not found!", id));
    }

}
