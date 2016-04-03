package rentit.com.inventory.application.dto;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.core.AnnotationMappingDiscoverer;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriTemplate;

import rentit.com.inventory.domain.model.PlantInvEntry;
import rentit.com.inventory.rest.PlantInventoryRestController;

@Service
public class PlantInvEntryAssembler extends ResourceAssemblerSupport<PlantInvEntry, PlantInvEntryDTO> {
	
	UriTemplate uriTemplate;

	public PlantInvEntryAssembler() {
        super(PlantInventoryRestController.class, PlantInvEntryDTO.class);

        AnnotationMappingDiscoverer discoverer = new AnnotationMappingDiscoverer(RequestMapping.class);
        try {
            String mapping = discoverer.getMapping(PlantInventoryRestController.class,
            		PlantInventoryRestController.class.getMethod("show", Long.class)); 

            uriTemplate = new UriTemplate(mapping);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
	
    public long resolveId(Link link) {
        return Long.parseLong(uriTemplate.match(link.getHref()).get("id"));
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
