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

import rentit.com.common.exceptions.ExtensionNotFound;
import rentit.com.common.exceptions.InvalidFieldException;
import rentit.com.common.exceptions.PlantNotFoundException;
import rentit.com.common.exceptions.PurchaseOrderNotFoundException;
import rentit.com.common.exceptions.dto.RentitExceptionDTO;
import rentit.com.sales.application.dto.PurchaseOrderDTO;
import rentit.com.sales.application.service.SalesService;
import rentit.com.sales.domain.model.PurchaseOrder.POStatus;


@RestController
@RequestMapping("/api/sales/orders")
public class PurchaseOrderRestController {
    
    @Autowired
    private SalesService salesService;
    
    @RequestMapping(method = RequestMethod.GET, path = "")
    public Collection<PurchaseOrderDTO> findAllPOs(){
    	return salesService.fetchAllPOs();
    }
    
    /**
     * Method that allows the modification of rejected Purchase order.
     * Currently PO can only be rejected when non-existing Plant ID is used or if the plant has become unavailable.
     */   
    @RequestMapping(method = RequestMethod.PUT, path = "/{id}")
    public ResponseEntity<PurchaseOrderDTO> modifyPurchaseOrder(@PathVariable Long id, @RequestBody PurchaseOrderDTO partialPoDto) throws PurchaseOrderNotFoundException, InvalidFieldException, URISyntaxException, PlantNotFoundException {
    	PurchaseOrderDTO po = salesService.modifyPO(partialPoDto);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(po.getId().getHref()));

        return new ResponseEntity<PurchaseOrderDTO>(po, headers, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PurchaseOrderDTO show(@PathVariable("id") Long id) throws PurchaseOrderNotFoundException {
    	return salesService.fetchPurchaseOrder(id);
    }

    @RequestMapping(method = RequestMethod.POST, path = "")
    public ResponseEntity<PurchaseOrderDTO> createPurchaseOrder(@RequestBody PurchaseOrderDTO partialPoDto) throws URISyntaxException, InvalidFieldException, PlantNotFoundException {
    	PurchaseOrderDTO po = salesService.createAndProcessPO(partialPoDto);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(po.getId().getHref()));

        return new ResponseEntity<PurchaseOrderDTO>(po, headers, HttpStatus.CREATED);
    }
    
	@RequestMapping(method=RequestMethod.POST, path = "/{id}/accept")
	public PurchaseOrderDTO acceptPurchaseOrder(@PathVariable Long id) throws PurchaseOrderNotFoundException {
		return salesService.modifyPoState(id, POStatus.OPEN);
	}

	@RequestMapping(method=RequestMethod.DELETE, path = "/{id}/accept")
	public PurchaseOrderDTO rejectPurchaseOrder(@PathVariable Long id) throws PurchaseOrderNotFoundException {
		return salesService.modifyPoState(id, POStatus.REJECTED);
	}
	
	@RequestMapping(method=RequestMethod.DELETE, path = "/{id}")
	public PurchaseOrderDTO closePurchaseOrder(@PathVariable Long id) throws PurchaseOrderNotFoundException {
		return salesService.modifyPoState(id, POStatus.CLOSED);
	}
	
	@RequestMapping(method=RequestMethod.POST, path = "/{id}/extensions")
	public PurchaseOrderDTO extendRentalPeriod(@PathVariable Long id, @RequestBody PurchaseOrderDTO partialPoDto) throws PurchaseOrderNotFoundException {
		return salesService.extendPoRentalPeriod(id, partialPoDto.getRentalPeriod().getEndDate());
	}
	
	@RequestMapping(method=RequestMethod.DELETE, path = "/{oid}/extensions/{eid}/accept")
	public PurchaseOrderDTO rejectRpExtension(@PathVariable Long oid, @PathVariable Long eid) throws PurchaseOrderNotFoundException, ExtensionNotFound {
		return salesService.handleExtension(oid, eid, false);
	}
	
	@RequestMapping(method=RequestMethod.POST, path = "/{oid}/extensions/{eid}/accept")
	public PurchaseOrderDTO acceptRpExtension(@PathVariable Long oid, @PathVariable Long eid) throws PurchaseOrderNotFoundException, ExtensionNotFound {
		return salesService.handleExtension(oid, eid, true);
	}
	
    @ExceptionHandler(InvalidFieldException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handInvalidField(InvalidFieldException ex) {
	}
    
	@ExceptionHandler({PlantNotFoundException.class, PurchaseOrderNotFoundException.class, ExtensionNotFound.class})
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public RentitExceptionDTO handNotFoundException(Exception ex) {
		if( ex instanceof PlantNotFoundException && ((PlantNotFoundException) ex).getUri() != null){
			PlantNotFoundException e = (PlantNotFoundException) ex;
			return RentitExceptionDTO.of(e.getMessage(), e.getUri());
		}
		return null;
	}
	
}