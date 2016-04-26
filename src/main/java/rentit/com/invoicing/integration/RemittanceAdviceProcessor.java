package rentit.com.invoicing.integration;

import java.io.ByteArrayInputStream;

import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import rentit.com.sales.application.dto.PurchaseOrderAssembler;
import rentit.com.sales.domain.model.Invoice;
import rentit.com.sales.domain.model.Invoice.InvoiceStatus;
import rentit.com.sales.domain.repository.InvoiceRepository;

@Service
public class RemittanceAdviceProcessor {
	
	@Autowired
	private InvoiceRepository invoiceRepo;
	
	@Autowired
	private PurchaseOrderAssembler poAssembler;
	
	public void processRemittanceAdvice(String remittanceAdvice) throws Exception {
		System.out.println("About to process remittance advice:\n" + remittanceAdvice);
		
		Document doc = loadXMLFromString(remittanceAdvice);
		doc.getDocumentElement().normalize();
		
		Element root = doc.getDocumentElement();
		String poRef = root.getElementsByTagName("purchaseOrderHRef").item(0).getTextContent();
		
		Invoice invoice = invoiceRepo.findActiveInvoiceByPoId(poAssembler.resolveIdByHref(poRef));
		invoice.setStatus(InvoiceStatus.PAID);
		invoiceRepo.save(invoice);
	}

	public String extractRemittanceAdvice(MimeMessage msg) throws Exception {
		Multipart multipart = (Multipart) msg.getContent();
		for (int i = 0; i < multipart.getCount(); i++) {
			BodyPart bodyPart = multipart.getBodyPart(i);
			if (bodyPart.getContentType().contains("xml") && bodyPart.getFileName().startsWith("invoice"))
				return IOUtils.toString(bodyPart.getInputStream(), "UTF-8");
		}
		throw new Exception("Oops ... no invoice found in email");
	}
	
	private static Document loadXMLFromString(String xml) throws Exception{
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		return builder.parse(new ByteArrayInputStream(xml.getBytes()));
	}
}
