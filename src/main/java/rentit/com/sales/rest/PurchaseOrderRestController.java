package rentit.com.sales.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import rentit.com.common.domain.model.BusinessPeriod;
import rentit.com.common.exceptions.ExtensionNotFound;
import rentit.com.common.exceptions.InvalidFieldException;
import rentit.com.common.exceptions.PlantNotFoundException;
import rentit.com.common.exceptions.PurchaseOrderNotFoundException;
import rentit.com.sales.application.dto.ExtensionDTO;
import rentit.com.sales.application.dto.PurchaseOrderAssembler;
import rentit.com.sales.application.dto.PurchaseOrderDTO;
import rentit.com.sales.application.service.SalesService;
import rentit.com.sales.domain.model.PurchaseOrder;
import rentit.com.sales.domain.model.PurchaseOrder.POStatus;

@RestController
@RequestMapping("/api/sales/orders")
public class PurchaseOrderRestController {
    
    @Autowired
    private SalesService salesService;
    
    @Autowired
    private PurchaseOrderAssembler poAssembler;
    

    @RequestMapping(method = RequestMethod.GET, path = "")
    public Collection<PurchaseOrderDTO> findAllPOs(){
    	return poAssembler.toResources(salesService.fetchAllPOs());
    }
    
    /**
     * Method that allows the modification of rejected Purchase order.
     * Currently PO can only be rejected when non-existing Plant ID is used or if the plant has become unavailable.
     */   
    @RequestMapping(method = RequestMethod.PUT, path = "/{id}")
    public ResponseEntity<PurchaseOrderDTO> modifyPurchaseOrder(@RequestBody PurchaseOrderDTO partialPoDto) throws PurchaseOrderNotFoundException, InvalidFieldException, URISyntaxException, PlantNotFoundException {
        PurchaseOrder po = salesService.modifyPO(partialPoDto.getPoId(), BusinessPeriod.fromDto(partialPoDto.getRentalPeriod()));
    	PurchaseOrderDTO newPoDto = poAssembler.toResource(po);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(newPoDto.getId().getHref()));

        return new ResponseEntity<PurchaseOrderDTO>(newPoDto, headers, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PurchaseOrderDTO fetchPurchaseOrder(@PathVariable("id") Long id) throws PurchaseOrderNotFoundException {
    	return poAssembler.toResource(salesService.fetchPurchaseOrder(id));
    }

    @RequestMapping(method = RequestMethod.POST, path = "")
    public ResponseEntity<PurchaseOrderDTO> createPurchaseOrder(@RequestBody PurchaseOrderDTO partialPoDto) throws URISyntaxException, InvalidFieldException, PlantNotFoundException {
    	PurchaseOrder po = salesService.createAndProcessPO(partialPoDto.getPlantId(), BusinessPeriod.fromDto(partialPoDto.getRentalPeriod()));
    	PurchaseOrderDTO newPoDto = poAssembler.toResource(po);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(newPoDto.getId().getHref()));

        return new ResponseEntity<PurchaseOrderDTO>(newPoDto, headers, HttpStatus.CREATED);
    }
    
	@RequestMapping(method=RequestMethod.POST, path = "/{id}/accept")
	public PurchaseOrderDTO acceptPurchaseOrder(@PathVariable Long id) throws PurchaseOrderNotFoundException {
		return poAssembler.toResource(salesService.modifyPoState(id, POStatus.OPEN));
	}

	@RequestMapping(method=RequestMethod.DELETE, path = "/{id}/accept")
	public PurchaseOrderDTO rejectPurchaseOrder(@PathVariable Long id) throws PurchaseOrderNotFoundException {
		return poAssembler.toResource(salesService.modifyPoState(id, POStatus.REJECTED));
	}
	
	@RequestMapping(method=RequestMethod.DELETE, path = "/{id}")
	public PurchaseOrderDTO closePurchaseOrder(@PathVariable Long id) throws PurchaseOrderNotFoundException {
		return poAssembler.toResource(salesService.modifyPoState(id, POStatus.CLOSED));
	}
	
	@RequestMapping(method=RequestMethod.POST, path = "/{id}/extensions")
	public PurchaseOrderDTO extendRentalPeriod(@PathVariable Long id, @RequestBody ExtensionDTO extension) throws PurchaseOrderNotFoundException {
		return poAssembler.toResource(salesService.extendPoRentalPeriod(extension.getPoId(), extension.getNewEndDate()));
	}
	
	@RequestMapping(method=RequestMethod.DELETE, path = "/{oid}/extensions/{eid}/accept")
	public PurchaseOrderDTO rejectRpExtension(@PathVariable Long oid, @PathVariable Long eid) throws PurchaseOrderNotFoundException, ExtensionNotFound {
		return poAssembler.toResource(salesService.handleExtension(oid, eid, false));
	}
	
	@RequestMapping(method=RequestMethod.POST, path = "/{oid}/extensions/{eid}/accept")
	public PurchaseOrderDTO acceptRpExtension(@PathVariable Long oid, @PathVariable Long eid) throws PurchaseOrderNotFoundException, ExtensionNotFound {
		return poAssembler.toResource(salesService.handleExtension(oid, eid, true));
	}
    
    @ExceptionHandler(InvalidFieldException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handInvalidField(InvalidFieldException ex) {
	}
    
	@ExceptionHandler({PlantNotFoundException.class, PurchaseOrderNotFoundException.class, ExtensionNotFound.class})
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public void handNotFoundException(Exception ex) {
	}
	
}