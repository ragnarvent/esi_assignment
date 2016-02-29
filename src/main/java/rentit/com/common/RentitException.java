package rentit.com.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RentitException extends RuntimeException {
	private static final long serialVersionUID = 3088559746512334236L;
	private String message;
}
