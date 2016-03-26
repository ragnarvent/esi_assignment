package rentit.com.inventory.application.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rentit.com.common.domain.model.BusinessPeriod;
import rentit.com.inventory.domain.model.PlantInvEntry;
import rentit.com.inventory.domain.repository.PlantInvEntryRepository;

@Service
public class PlantCatalogService {
	
	@Autowired
	private PlantInvEntryRepository plantEntryRepo;
	
	public Collection<PlantInvEntry> findAvailablePlants( String name, BusinessPeriod period){
		return plantEntryRepo.findAvailablePlants(name, period.getStartDate(), period.getEndDate());
	}
}
