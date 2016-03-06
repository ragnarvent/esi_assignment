package rentit.com.web.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;

import rentit.com.inventory.domain.PlantInvEntry;
import rentit.com.inventory.domain.PlantInvEntryRepository;
import rentit.com.sales.domain.PurchaseOrder;

@Service
public class PurchaseOrderAssembler extends ResourceAssemblerSupport<PurchaseOrder, PurchaseOrderDTO> {

	@Autowired
	PlantInvEntryRepository entryRepo;

	@Autowired
	CommonDTOAssembler commonAssembler;

	public PurchaseOrderAssembler() {
		super(PurchaseOrder.class, PurchaseOrderDTO.class);
	}

	@Override
	public PurchaseOrderDTO toResource(PurchaseOrder order) {
		PurchaseOrderDTO dto = createResourceWithId(order.getId(), order);
		dto.setPlantId(order.getId());
		dto.setPoId(order.getId());

		PlantInvEntry plantEntry = entryRepo.findOne(order.getPlantEntryId());
		dto.setName(plantEntry.getName());
		dto.setDescription(plantEntry.getDescription());

		dto.setCost(order.getTotal());
		dto.setStatus(order.getStatus());
		dto.setRentalPeriod(commonAssembler.businessPeriodToDTO(order.getRentalPeriod()));
		return dto;
	}
}
