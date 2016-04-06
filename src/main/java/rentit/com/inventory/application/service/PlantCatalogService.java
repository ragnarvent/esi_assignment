package rentit.com.inventory.application.service;

import java.util.Collection;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;

import rentit.com.common.domain.model.BusinessPeriod;
import rentit.com.common.domain.validation.BusinessPeriodValidator;
import rentit.com.common.exceptions.InvalidFieldException;
import rentit.com.common.exceptions.PlantNotFoundException;
import rentit.com.inventory.application.dto.PlantInvEntryAssembler;
import rentit.com.inventory.application.dto.PlantInvEntryDTO;
import rentit.com.inventory.domain.model.PlantInvEntry;
import rentit.com.inventory.domain.repository.PlantInvEntryRepository;

@Service
public class PlantCatalogService {
	
	@Autowired
	private PlantInvEntryRepository plantEntryRepo;
	
	@Autowired
	PlantInvEntryAssembler entryAssembler;
	
	public Collection<PlantInvEntryDTO> findAvailablePlants( String name, BusinessPeriod period) throws InvalidFieldException{
		validateBusinessPeriod(period);
		return entryAssembler.toResources(plantEntryRepo.findAvailablePlants(name, period.getStartDate(), period.getEndDate()));
	}

	public PlantInvEntryDTO findPlant(Long id) throws PlantNotFoundException {
		PlantInvEntry entry = plantEntryRepo.findOne(id);
		if(entry == null)
			throw new PlantNotFoundException(id, null);
		return entryAssembler.toResource(entry);
	}
	
	private static void validateBusinessPeriod( BusinessPeriod period) throws InvalidFieldException{
		DataBinder binder = new DataBinder(period);
		binder.addValidators(new BusinessPeriodValidator());
		binder.validate();
		
		if(binder.getBindingResult().hasFieldErrors()){
			throw new InvalidFieldException(binder.getBindingResult().getFieldError().getDefaultMessage());
		}
	}
	
	public PlantInvEntry findPlantFullRepresentation(PlantInvEntryDTO plant) throws PlantNotFoundException {
	    long id = entryAssembler.resolveId(Objects.requireNonNull(plant.getLink("self")));
	    return plantEntryRepo.findOne(id);
	}
}
