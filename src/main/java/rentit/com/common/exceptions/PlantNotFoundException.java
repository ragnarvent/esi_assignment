package rentit.com.common.exceptions;

public class PlantNotFoundException extends Exception {
	private static final long serialVersionUID = -956251448935690817L;

	public PlantNotFoundException(Long id) {
        super(String.format("Plant not found! (Plant id: %d)", id));
    }
}
