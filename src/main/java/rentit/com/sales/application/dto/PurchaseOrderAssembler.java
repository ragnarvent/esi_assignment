package rentit.com.sales.application.dto;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;

import rentit.com.common.application.dto.BusinessPeriodDTOAssembler;
import rentit.com.common.rest.ExtendedLink;
import rentit.com.inventory.domain.model.PlantInvEntry;
import rentit.com.inventory.domain.repository.PlantInvEntryRepository;
import rentit.com.sales.domain.model.PurchaseOrder;
import rentit.com.sales.rest.SalesRestController;



@Service
public class PurchaseOrderAssembler extends ResourceAssemblerSupport<PurchaseOrder, PurchaseOrderDTO> {

	@Autowired
	PlantInvEntryRepository entryRepo;

	@Autowired
	BusinessPeriodDTOAssembler periodAssembler;

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
		dto.setRentalPeriod(periodAssembler.businessPeriodToDTO(order.getRentalPeriod()));
		
        try {
            switch (order.getStatus()) {
                case PENDING:
                    dto.add(new ExtendedLink(
                            linkTo(methodOn(SalesRestController.class)
                              .acceptPurchaseOrder(dto.getPlantId())).toString(),
                            "accept", POST));
                    dto.add(new ExtendedLink(
                            linkTo(methodOn(SalesRestController.class)
                              .rejectPurchaseOrder(dto.getPlantId())).toString(),
                            "reject", DELETE));
                    break;
               default: break;
            }
        } catch (Exception _skip) {}
		return dto;
	}
}
