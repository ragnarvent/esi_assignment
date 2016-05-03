package rentit.com.invoicing.dto;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.HttpMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;

import rentit.com.common.exceptions.PurchaseOrderNotFoundException;
import rentit.com.common.rest.ExtendedLink;
import rentit.com.invoicing.rest.InvoiceController;
import rentit.com.sales.application.service.SalesService;
import rentit.com.sales.domain.model.Invoice;

@Service
public class InvoiceAssembler extends ResourceAssemblerSupport<Invoice, InvoiceDTO> {
	
	@Autowired
	private SalesService salesService;

	public InvoiceAssembler() {
		super(InvoiceController.class, InvoiceDTO.class);
	}

	@Override
	public InvoiceDTO toResource(Invoice invoice) {
		final long invoiceId = invoice.getId();
		InvoiceDTO dto = createResourceWithId(invoiceId, invoice);

		dto.setTotal(invoice.getTotal());
		dto.setStatus(invoice.getStatus());

		try {
			dto.add(new Link(salesService.fetchPurchaseOrder(invoice.getPoId()).getLink("self").getHref(), "purchaseorder"));
		} catch (PurchaseOrderNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			switch(invoice.getStatus()){
				case PAID:
	               	 dto.add(new ExtendedLink(
	                         linkTo(methodOn(InvoiceController.class)
	                           .remindUnpaidInvoice(invoice.getId(), null)).toString(),
	                         "remind", POST));
					break;
				default: break;
			}
		} catch(Exception _skip){}

		return dto;
	}
}