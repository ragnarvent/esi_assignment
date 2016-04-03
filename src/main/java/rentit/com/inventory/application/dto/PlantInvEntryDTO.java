package rentit.com.inventory.application.dto;

import java.math.BigDecimal;

import org.springframework.hateoas.ResourceSupport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor(staticName="of")
public class PlantInvEntryDTO extends ResourceSupport{
	private String name;
	private String description;
	private BigDecimal price;
}
