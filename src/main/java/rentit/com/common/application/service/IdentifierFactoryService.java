package rentit.com.common.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rentit.com.infrastructure.HibernateBasedIdentifierGenerator;

@Service
public class IdentifierFactoryService {
    
	@Autowired
    private HibernateBasedIdentifierGenerator idGenerator;

    public long nextPurchaseOrderID() {
        return idGenerator.getID("PurchaseOrderIDSequence");
    }
    
    public long nextPlantReservationID(){
    	return idGenerator.getID("PlantReservationSequence");
    }
    
    public long nextPoExtensionID(){
    	return idGenerator.getID("PoExtensionIDSequence");
    }
    
    public long nextInvoiceID(){
    	return idGenerator.getID("InvoiceIDSequence");
    }
}
