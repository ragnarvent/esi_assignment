package rentit.com.web.dto;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import rentit.com.common.domain.BusinessPeriod;
import rentit.com.inventory.domain.PlantInvEntry;
import rentit.com.sales.domain.PurchaseOrder;

@Service
public class DTOAssembler {
	
	public List<PlantInvEntryDTO> plantEntriesToDTO(List<PlantInvEntry> entries){
		return entries.stream().map(e->PlantInvEntryDTO.of(e.getId(), e.getName(), e.getDescription(), e.getPrice())).collect(Collectors.toList());
	}
	
	public BusinessPeriod businessPeriodFromDTO(BusinessPeriodDTO periodDTO){
		return BusinessPeriod.of(periodDTO.getStartDate(), periodDTO.getEndDate() );
	}
	
	public BusinessPeriodDTO businessPeriodToDTO(BusinessPeriod period){
		return BusinessPeriodDTO.of(period.getStartDate(), period.getEndDate() );
	}
	
	public PurchaseOrderDTO purchaseOrderToDTO(PurchaseOrder po, String name, String description) {
		return PurchaseOrderDTO.of(po.getPlantEntryId(), name, description, po.getTotal(), po.getStatus(), businessPeriodToDTO(po.getRentalPeriod()));
	}
}
