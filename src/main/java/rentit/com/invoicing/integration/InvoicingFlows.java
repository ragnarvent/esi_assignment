package rentit.com.invoicing.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.mail.Mail;

@Configuration
public class InvoicingFlows {

	@Value("${gmail.username}")
	String gmailUsername;

	@Value("${gmail.password}")
	String gmailPassword;

	@Bean
	IntegrationFlow sendInvoiceFlow() {
		return IntegrationFlows.from("sendInvoiceChannel")
				.handle(Mail.outboundAdapter("smtp.gmail.com").port(465).protocol("smtps")
						.credentials(gmailUsername, gmailPassword)
						.javaMailProperties(p -> p.put("mail.debug", "false")))
				.get();
	}
	
	@Bean
	IntegrationFlow processInvoiceFlow() {
		return IntegrationFlows
				.from(Mail.imapIdleAdapter(
								String.format("imaps://%s:%s@imap.gmail.com/INBOX", gmailUsername, gmailPassword))
						.selectorExpression("subject matches '.*[rR]emittance.*'"))
				.transform("@remittanceAdviceProcessor.extractRemittanceAdvice(payload)")
				.handle("remittanceAdviceProcessor", "processRemittanceAdvice")
//				.route("#xpath(payload, 'string')",
//						mapping -> mapping.subFlowMapping("true", sf -> sf.handle("remittanceAdviceProcessor", "processRemittanceAdvice"))
//								.subFlowMapping("false", sf -> sf.handle(System.out::println)))
				.get();
	}

}
