package rentit.com.sales.application.dto;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;

import rentit.com.common.application.dto.BusinessPeriodDTO;
import rentit.com.common.rest.ExtendedLink;
import rentit.com.inventory.application.dto.PlantInvEntryAssembler;
import rentit.com.inventory.domain.model.PlantInvEntry;
import rentit.com.inventory.domain.repository.PlantInvEntryRepository;
import rentit.com.sales.domain.model.PurchaseOrder;
import rentit.com.sales.rest.PurchaseOrderRestController;



@Service
public class PurchaseOrderAssembler extends ResourceAssemblerSupport<PurchaseOrder, PurchaseOrderDTO> {

	@Autowired
	PlantInvEntryRepository entryRepo;
	
	@Autowired
	PlantInvEntryAssembler plantEntryAssembler;

	public PurchaseOrderAssembler() {
		super(PurchaseOrder.class, PurchaseOrderDTO.class);
	}

	@Override
	public PurchaseOrderDTO toResource(PurchaseOrder order) {
		PurchaseOrderDTO dto = createResourceWithId(order.getId(), order);
		dto.setPoId(order.getId());

		PlantInvEntry plantEntry = entryRepo.findOne(order.getPlantEntryId());
		dto.setPlant(plantEntryAssembler.toResource(plantEntry));

		dto.setCost(order.getTotal());
		dto.setStatus(order.getStatus());
		dto.setRentalPeriod(BusinessPeriodDTO.toDto(order.getRentalPeriod()));
		
        try {
            switch (order.getStatus()) {
                case PENDING_CONFIRMATION:
                    dto.add(new ExtendedLink(
                            linkTo(methodOn(PurchaseOrderRestController.class)
                              .acceptPurchaseOrder(dto.getPoId())).toString(),
                            "accept", POST));
                    dto.add(new ExtendedLink(
                            linkTo(methodOn(PurchaseOrderRestController.class)
                              .rejectPurchaseOrder(dto.getPoId())).toString(),
                            "reject", DELETE));
                    break;
                case PENDING_EXTENSION:
                    dto.add(new ExtendedLink(
                            linkTo(methodOn(PurchaseOrderRestController.class)
                              .acceptRpExtension(dto.getPoId(), order.getExtension().getExtensionId())).toString(),
                            "accept", POST));
                    dto.add(new ExtendedLink(
                            linkTo(methodOn(PurchaseOrderRestController.class)
                              .rejectRpExtension(dto.getPoId(), order.getExtension().getExtensionId())).toString(),
                            "reject", DELETE));
                	break;
               default: break;
            }
        } catch (Exception _skip) {}
		return dto;
	}
}
