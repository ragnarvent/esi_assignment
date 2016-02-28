package rentit.com.inventory.application;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rentit.com.common.domain.BusinessPeriod;
import rentit.com.inventory.domain.PlantInvEntry;
import rentit.com.inventory.domain.PlantInvEntryRepository;

@Service
public class PlantCatalogService {
	
	@Autowired
	private PlantInvEntryRepository plantEntryRepo;
	
	public Collection<PlantInvEntry> findAvailablePlants( String name, BusinessPeriod period){
		return plantEntryRepo.findAvailablePlants(name, period.getStartDate(), period.getEndDate());
	}
}
