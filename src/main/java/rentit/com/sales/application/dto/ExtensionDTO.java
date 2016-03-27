package rentit.com.sales.application.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor(staticName="of")
public class ExtensionDTO {
	
	private long poId;
	
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate newEndDate;

}
