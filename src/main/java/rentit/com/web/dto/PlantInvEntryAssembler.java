package rentit.com.web.dto;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;

import rentit.com.inventory.domain.PlantInvEntry;

@Service
public class PlantInvEntryAssembler extends ResourceAssemblerSupport<PlantInvEntry, PlantInvEntryDTO> {

	public PlantInvEntryAssembler() {
		super(PlantInvEntry.class, PlantInvEntryDTO.class);
	}

	@Override
	public PlantInvEntryDTO toResource(PlantInvEntry plant) {
		PlantInvEntryDTO dto = createResourceWithId(plant.getId(), plant);
		dto.setEntryId(plant.getId());
		dto.setName(plant.getName());
		dto.setDescription(plant.getDescription());
		dto.setPrice(plant.getPrice());
		return dto;
	}
	
}
