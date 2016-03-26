package rentit.com.inventory.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rentit.com.common.application.service.IdentifierFactoryService;
import rentit.com.common.domain.model.BusinessPeriod;
import rentit.com.inventory.domain.model.PlantInvItem;
import rentit.com.inventory.domain.model.PlantReservation;
import rentit.com.inventory.domain.model.PlantInvItem.EquipmentCondition;
import rentit.com.inventory.domain.repository.PlantInvItemRepository;
import rentit.com.inventory.domain.repository.PlantReservationRepository;

@Service
public class InventoryService {
	
	@Autowired
	private PlantInvItemRepository plantItemRepo;
	
	@Autowired
	private PlantReservationRepository reservationRepo;
	
	@Autowired
	private IdentifierFactoryService idFactory;

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
