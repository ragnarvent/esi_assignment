package rentit.com.sales.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rentit.com.infrastructure.HibernateBasedIdentifierGenerator;

@Service
public class IdentifierFactory {
    @Autowired
    HibernateBasedIdentifierGenerator idGenerator;

    public long nextPurchaseOrderID() {
        return idGenerator.getID("PurchaseOrderIDSequence");
    }
    
    public long nextPlantReservationID(){
    	return idGenerator.getID("PlantReservationSequence");
    }
}
