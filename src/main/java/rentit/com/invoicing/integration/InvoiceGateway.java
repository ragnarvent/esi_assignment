package rentit.com.invoicing.integration;

import javax.mail.internet.MimeMessage;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface InvoiceGateway {
	@Gateway(requestChannel = "sendInvoiceChannel")
	public void sendInvoice(MimeMessage msg);
}
