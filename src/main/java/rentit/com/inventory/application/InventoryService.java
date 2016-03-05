package rentit.com.inventory.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rentit.com.common.domain.BusinessPeriod;
import rentit.com.inventory.domain.PlantInvItem;
import rentit.com.inventory.domain.PlantInvItem.EquipmentCondition;
import rentit.com.inventory.domain.PlantInvItemRepository;
import rentit.com.inventory.domain.PlantReservation;
import rentit.com.inventory.domain.PlantReservationRepository;
import rentit.com.sales.application.IdentifierFactory;

@Service
public class InventoryService {
	
	@Autowired
	private PlantInvItemRepository plantItemRepo;
	
	@Autowired
	private PlantReservationRepository reservationRepo;
	
	@Autowired
	private IdentifierFactory idFactory;

	public PlantReservation reservePlant(long poId, long plantEntryId, BusinessPeriod rentalPeriod) {
		//Find all available plant items and filter out the ones that are not serviceable(could also be done with SQL)
		List<PlantInvItem> plantItems = plantItemRepo.findAvailablePlantItems(plantEntryId, rentalPeriod.getStartDate(), rentalPeriod.getEndDate())
											.stream().filter(p->p.getCondition() == EquipmentCondition.SERVICEABLE).collect(Collectors.toList());
		if(plantItems.isEmpty())
			return null;
		
		//Create new reservation for first available plant
		PlantReservation reservation = PlantReservation.of(idFactory.nextPlantReservationID(), plantItems.get(0), rentalPeriod);
		reservation.setRentalId(poId);
		reservationRepo.save(reservation);
		
		return reservation;
	}
}
