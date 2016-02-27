package rentit.com.sales.domain;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Embeddable
@Value
@NoArgsConstructor(force=true,access=lombok.AccessLevel.PRIVATE)
@AllArgsConstructor(staticName="of")
public class Address {
	private String address;
}
