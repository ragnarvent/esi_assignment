package rentit.com.inventory.rest;

import java.time.LocalDate;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import rentit.com.common.domain.model.BusinessPeriod;
import rentit.com.common.exceptions.InvalidFieldException;
import rentit.com.common.exceptions.PlantNotFoundException;
import rentit.com.inventory.application.dto.PlantInvEntryDTO;
import rentit.com.inventory.application.service.PlantCatalogService;

@RestController
@RequestMapping("/api/inventory/plants")
public class PlantInventoryRestController {
	
	@Autowired
    private PlantCatalogService catalogService;
	
    @RequestMapping(method = RequestMethod.GET, path = "")
    public Collection<PlantInvEntryDTO> findAvailablePlants(
            @RequestParam(name = "name") String plantName,
            @RequestParam(name = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
            ) throws InvalidFieldException {
    	return catalogService.findAvailablePlants(plantName, BusinessPeriod.of(startDate, endDate));
    }
    
    @RequestMapping(method = RequestMethod.GET, path = "/{id}")
    public PlantInvEntryDTO show(@PathVariable Long id) throws PlantNotFoundException {
            return catalogService.findPlant(id);
    }
    
	@ExceptionHandler(PlantNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public void handPlantNotFoundException(PlantNotFoundException ex) {
	}
	
    @ExceptionHandler(InvalidFieldException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handInvalidField(InvalidFieldException ex) {
	}

}
