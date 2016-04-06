package rentit.com.sales.application.dto;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.core.AnnotationMappingDiscoverer;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriTemplate;

import rentit.com.common.application.dto.BusinessPeriodDTO;
import rentit.com.common.exceptions.PurchaseOrderNotFoundException;
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
	
	private UriTemplate uriTemplate;

	public PurchaseOrderAssembler() {
        super(PurchaseOrderRestController.class, PurchaseOrderDTO.class);

        AnnotationMappingDiscoverer discoverer = new AnnotationMappingDiscoverer(RequestMapping.class);
        try {
            String mapping = discoverer.getMapping(PurchaseOrderRestController.class,
            		PurchaseOrderRestController.class.getMethod("show", Long.class)); 

            uriTemplate = new UriTemplate(mapping);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
	}

	@Override
	public PurchaseOrderDTO toResource(PurchaseOrder order) {
		PurchaseOrderDTO dto = createResourceWithId(order.getId(), order);

		PlantInvEntry plantEntry = entryRepo.findOne(order.getPlantEntryId());
		dto.setPlant(plantEntryAssembler.toResource(plantEntry));

		dto.setCost(order.getTotal());
		dto.setStatus(order.getStatus());
		dto.setRentalPeriod(BusinessPeriodDTO.toDto(order.getRentalPeriod()));
		
        try {
            switch (order.getStatus()) {
            	case REJECTED:
            		dto.add(new ExtendedLink(
                            linkTo(methodOn(PurchaseOrderRestController.class)
                        		.modifyPurchaseOrder(order.getId(), null)).toString(),
                            "modify", PUT));
            		break;
                case PENDING_CONFIRMATION:
                    dto.add(new ExtendedLink(
                            linkTo(methodOn(PurchaseOrderRestController.class)
                              .acceptPurchaseOrder(order.getId())).toString(),
                            "accept", POST));
                    dto.add(new ExtendedLink(
                            linkTo(methodOn(PurchaseOrderRestController.class)
                              .rejectPurchaseOrder(order.getId())).toString(),
                            "reject", DELETE));
                    dto.add(createDeleteLink(order.getId())); //BuildIt can end PO prematurely
                    break;
                case PENDING_EXTENSION:
                    dto.add(new ExtendedLink(
                            linkTo(methodOn(PurchaseOrderRestController.class)
                              .acceptRpExtension(order.getId(), order.getExtension().getExtensionId())).toString(),
                            "acceptExtention", POST));
                    dto.add(new ExtendedLink(
                            linkTo(methodOn(PurchaseOrderRestController.class)
                              .rejectRpExtension(order.getId(), order.getExtension().getExtensionId())).toString(),
                            "rejectExtention", DELETE));
                	break;
                case OPEN:
                    dto.add(new ExtendedLink(
                            linkTo(methodOn(PurchaseOrderRestController.class)
                              .extendRentalPeriod(order.getId(), null)).toString(),
                            "extend", POST));
                	dto.add(createDeleteLink(order.getId()));
                	break;
               default: break;
            }
        } catch (Exception _skip) {}
		return dto;
	}
	
	private static ExtendedLink createDeleteLink(long orderId) throws PurchaseOrderNotFoundException{
		return new ExtendedLink(
                linkTo(methodOn(PurchaseOrderRestController.class)
                		.closePurchaseOrder(orderId)).toString(),
                      "close", DELETE);
	}
	
    public long resolveId(Link link) {
        return Long.parseLong(uriTemplate.match(link.getHref()).get("id"));
    }
}
