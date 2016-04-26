package rentit.com.sales.application.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;

import rentit.com.common.exceptions.PurchaseOrderNotFoundException;
import rentit.com.sales.application.service.SalesService;
import rentit.com.sales.domain.model.Invoice;
import rentit.com.sales.rest.PurchaseOrderRestController;

@Service
public class InvoiceAssembler extends ResourceAssemblerSupport<Invoice, InvoiceDTO> {
	
	@Autowired
	private SalesService salesService;

	public InvoiceAssembler() {
		super(PurchaseOrderRestController.class, InvoiceDTO.class);
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

		return dto;
	}
}
