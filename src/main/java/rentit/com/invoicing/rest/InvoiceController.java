package rentit.com.invoicing.rest;

import java.io.IOException;
import java.util.Collection;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import rentit.com.common.exceptions.InvoiceNotFoundException;
import rentit.com.common.exceptions.PurchaseOrderNotFoundException;
import rentit.com.invoicing.dto.InvoiceDTO;
import rentit.com.sales.application.dto.PurchaseOrderDTO;
import rentit.com.sales.application.service.InvoiceService;
import rentit.com.sales.application.service.SalesService;

@RestController
@RequestMapping("/api/sales/invoices")
public class InvoiceController {
	
	@Autowired
	private InvoiceService invoiceService;
	
	@Autowired
	private SalesService salesService;
	
	@RequestMapping(method=RequestMethod.POST, path = "/{oid}/invoices")
	public InvoiceDTO createInvoice(@PathVariable Long oid, @RequestBody PurchaseOrderDTO poDto) throws PurchaseOrderNotFoundException, MessagingException, IOException{
		PurchaseOrderDTO poDtoFull = salesService.fetchPurchaseOrder(oid);
		InvoiceDTO invoice = invoiceService.sendInvoice(oid, poDtoFull.getLink("self").getHref(), poDtoFull.getCost());
		return invoice;
	}
	
	@RequestMapping(method=RequestMethod.POST, path = "/{id}/invoices/remind")
	public InvoiceDTO remindUnpaidInvoice(@PathVariable Long id, @RequestBody InvoiceDTO invoiceDto) throws InvoiceNotFoundException, MessagingException, IOException, PurchaseOrderNotFoundException{
		return invoiceService.remindInvoice(id);
	}
	
    @RequestMapping(method = RequestMethod.GET, path = "/invoices")
    @ResponseStatus(HttpStatus.OK)
    public Collection<InvoiceDTO> findAllInvoices() {
    	return invoiceService.findAllInvoices();
    }
    
	@ExceptionHandler({PurchaseOrderNotFoundException.class, InvoiceNotFoundException.class})
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public void handNotFoundException(Exception ex) {
	}

}
