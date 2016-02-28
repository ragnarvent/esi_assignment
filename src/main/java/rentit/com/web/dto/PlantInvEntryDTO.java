package rentit.com.web.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName="of")
public class PlantInvEntryDTO {
	private long id;
	private String name;
	private String description;
	private BigDecimal price;
}
