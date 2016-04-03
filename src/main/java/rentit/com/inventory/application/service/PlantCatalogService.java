package rentit.com.inventory.application.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;

import rentit.com.common.domain.model.BusinessPeriod;
import rentit.com.common.domain.validation.BusinessPeriodValidator;
import rentit.com.common.exceptions.InvalidFieldException;
import rentit.com.common.exceptions.PlantNotFoundException;
import rentit.com.inventory.domain.model.PlantInvEntry;
import rentit.com.inventory.domain.repository.PlantInvEntryRepository;

@Service
public class PlantCatalogService {
	
	@Autowired
	private PlantInvEntryRepository plantEntryRepo;
	
	public Collection<PlantInvEntry> findAvailablePlants( String name, BusinessPeriod period) throws InvalidFieldException{
		validateBusinessPeriod(period);
		return plantEntryRepo.findAvailablePlants(name, period.getStartDate(), period.getEndDate());
	}

	public PlantInvEntry findPlant(Long id) throws PlantNotFoundException {
		PlantInvEntry entry = plantEntryRepo.findOne(id);
		if(entry == null)
			throw new PlantNotFoundException(id);
		return entry;
	}
	
	private static void validateBusinessPeriod( BusinessPeriod period) throws InvalidFieldException{
		DataBinder binder = new DataBinder(period);
		binder.addValidators(new BusinessPeriodValidator());
		binder.validate();
		
		if(binder.getBindingResult().hasFieldErrors()){
			throw new InvalidFieldException(binder.getBindingResult().getFieldError().getDefaultMessage());
		}
	}
	
}
