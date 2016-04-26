package rentit.com.sales.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import rentit.com.sales.domain.model.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

	@Query("select inv from Invoice inv where inv.poId = ?1 and inv.status='SENT'")
	public Invoice findActiveInvoiceByPoId(long resolveIdByHref);

}
