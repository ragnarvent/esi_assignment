package rentit.com.web.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import rentit.com.common.domain.BusinessPeriod;
import rentit.com.inventory.domain.PlantInvEntry;
import rentit.com.sales.domain.PurchaseOrder;

@Service
public class DTOAssembler {
	
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	public Collection<PlantInvEntryDTO> plantEntriesToDTO(Collection<PlantInvEntry> entries){
		return entries.stream().map(e->PlantInvEntryDTO.of(e.getId(), e.getName(), e.getDescription(), e.getPrice())).collect(Collectors.toSet());
	}
	
	public BusinessPeriod businessPeriodFromDTO(BusinessPeriodDTO periodDTO){
		return BusinessPeriod.of(LocalDate.parse(periodDTO.getStartDate(), formatter), LocalDate.parse(periodDTO.getEndDate()) );
	}
	
	public BusinessPeriodDTO businessPeriodToDTO(BusinessPeriod period){
		return BusinessPeriodDTO.of(period.getStartDate().toString(), period.getEndDate().toString() );
	}
	
	public PurchaseOrderDTO purchaseOrderToDTO(PurchaseOrder po, String name, String description) {
		return PurchaseOrderDTO.of(po.getPlantEntryId(), name, description, po.getTotal(), po.getStatus(), businessPeriodToDTO(po.getRentalPeriod()));
	}
}
