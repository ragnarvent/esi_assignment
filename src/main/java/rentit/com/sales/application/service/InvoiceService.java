package rentit.com.sales.application.service;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Service
public class InvoiceService {

	public MimeMessage composeMail(long poId, String poRef, BigDecimal total) throws MessagingException, IOException {
		JavaMailSender mailSender = new JavaMailSenderImpl();
		String invoice = buildXML(poRef, total);

		MimeMessage rootMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(rootMessage, true);
		helper.setFrom("rentit@gmail.com");
		helper.setTo("buildit@gmail.com");
		helper.setSubject(String.format("Invoice Purchase Order %d", poId));
		helper.setText(String.format(
				"Dear customer,\n\nPlease find attached the Invoice corresponding to your Purchase Order %d.\n\nKindly yours,\n\nRentIt Team!",
				poId));

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
}
