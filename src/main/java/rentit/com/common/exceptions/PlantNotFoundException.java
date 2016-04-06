package rentit.com.common.exceptions;

public class PlantNotFoundException extends Exception {
	private static final long serialVersionUID = -956251448935690817L;
	
	private final String uri;

	public PlantNotFoundException(Long id, String uri) {
        super(String.format("Plant not found! (Plant id: %d)", id));
        this.uri = uri;
    }

	public String getUri() {
		return uri;
	}
	
}
