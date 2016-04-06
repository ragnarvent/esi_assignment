package rentit.com.common.exceptions;

public class PurchaseOrderNotFoundException extends Exception{
	private static final long serialVersionUID = 7548409766113033664L;

	public PurchaseOrderNotFoundException(Long id) {
        super(String.format("Purchase order not found! (Purchase order id: %d)", id));
    }
}
