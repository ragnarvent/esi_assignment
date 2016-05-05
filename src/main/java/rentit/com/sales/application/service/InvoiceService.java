package rentit.com.sales.application.service;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collection;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import rentit.com.common.application.service.IdentifierFactoryService;
import rentit.com.common.exceptions.InvoiceNotFoundException;
import rentit.com.common.exceptions.PurchaseOrderNotFoundException;
import rentit.com.invoicing.dto.InvoiceAssembler;
import rentit.com.invoicing.dto.InvoiceDTO;
import rentit.com.invoicing.integration.InvoiceGateway;
import rentit.com.sales.domain.model.Invoice;
import rentit.com.sales.domain.repository.InvoiceRepository;

@Service
public class InvoiceService {
	
	@Autowired
	private InvoiceRepository invoiceRepo;
	
    @Autowired
    private InvoiceGateway invoiceGw;
    
    @Autowired
    private IdentifierFactoryService idFactory;
    
    @Autowired
    private InvoiceAssembler invoiceAssembler;
    
	public Collection<InvoiceDTO> findAllInvoices() {
		return invoiceAssembler.toResources(invoiceRepo.findAll());
	}
    
    public InvoiceDTO sendInvoice(long poId, String poRef, BigDecimal total) throws MessagingException, IOException {
    	String subject = String.format("Invoice Purchase Order %d", poId);
    	String text = String.format( 
    			"Dear customer,\n\nPlease find attached the Invoice corresponding to your Purchase Order %d.\n\nKindly yours,\n\nRentIt Team!",
				poId);
    	MimeMessage msg = composeMail(poId, poRef, total, subject, text);
    	
    	invoiceGw.sendInvoice(msg);
    	
    	Invoice invoice = Invoice.of(idFactory.nextInvoiceID(), poId, total);
    	invoiceRepo.save(invoice);
    	
    	return invoiceAssembler.toResource(invoice);
    }

	private MimeMessage composeMail(long poId, String poRef, BigDecimal total, String subject, String text) throws MessagingException, IOException {
		JavaMailSender mailSender = new JavaMailSenderImpl();
		String invoice = buildXML(poRef, total);

		MimeMessage rootMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(rootMessage, true);
		helper.setFrom("rentitbuildit@gmail.com");
		helper.setTo("builditapplic@gmail.com");
		helper.setSubject(subject);
		helper.setText(text);

		helper.addAttachment(String.format("invoice-po-%d.xml", poId), new ByteArrayDataSource(invoice, "application/xml"));
		
		return rootMessage;
	}

	private static String buildXML(String poHref, BigDecimal total) {
		try {
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			Document doc = dBuilder.newDocument();
			Element rootElement = doc.createElement("invoice");
			doc.appendChild(rootElement);

			Element poHrefElem = doc.createElement("purchaseOrderHRef");
			poHrefElem.appendChild(doc.createTextNode(poHref));
			rootElement.appendChild(poHrefElem);

			Element totalElem = doc.createElement("total");
			totalElem.appendChild(doc.createTextNode(new DecimalFormat("#0.##").format(total)));
			rootElement.appendChild(totalElem);

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			Writer out = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(out));
			return out.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	private Invoice findInvoice(Long id) throws InvoiceNotFoundException{
		Invoice invoice = invoiceRepo.findOne(id);
		if( invoice == null ){
			throw new InvoiceNotFoundException(id);
		}
		return invoice;
	}

	public InvoiceDTO remindInvoice(Long id) throws InvoiceNotFoundException, MessagingException, IOException, PurchaseOrderNotFoundException {
		Invoice invoice = findInvoice(id);
		
    	String subject = String.format("Reminder of an unpaid invoice for purchase order %d ", invoice.getPoId());
    	String text = String.format( 
    			"Dear customer,\n\nWe kindly remind you of an unpaid invoice with regard to purchase order %d.\n\nRentIt Team!",
    			invoice.getPoId());
    	
    	InvoiceDTO dto = invoiceAssembler.toResource(invoice);
		MimeMessage msg = composeMail(invoice.getPoId(), dto.getLink("purchaseorder").getHref(), invoice.getTotal(), subject, text);
		
		invoiceGw.sendInvoice(msg);
		
		return dto;
	}
}
