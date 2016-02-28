package rentit.com.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import rentit.com.common.InvalidException;
import rentit.com.inventory.application.PlantCatalogService;
import rentit.com.inventory.domain.PlantInvEntry;
import rentit.com.sales.application.SalesService;
import rentit.com.sales.domain.PurchaseOrder;
import rentit.com.web.dto.CatalogQueryDTO;
import rentit.com.web.dto.DTOAssembler;
import rentit.com.web.dto.PurchaseOrderDTO;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

	@Autowired
	PlantCatalogService plantCatalog;

	@Autowired
	SalesService salesService;
	
	@Autowired
	DTOAssembler dtoAssembler;
	
	@RequestMapping("/catalog/query")
	String executeQuery(CatalogQueryDTO query, Model model) {
		List<PlantInvEntry> entries = plantCatalog.findAvailablePlants(query.getName(), dtoAssembler.businessPeriodFromDTO(query.getRentalPeriod()));
		model.addAttribute("plants", dtoAssembler.plantEntriesToDTO(entries));
		return "dashboard/catalog/query-result";
	}

	@RequestMapping("/orders")
	String createPO(PurchaseOrderDTO poDTO, Model model) {
		PurchaseOrder po;
		try {
			po = salesService.createAndProcessPO(poDTO.getPlantId(), dtoAssembler.businessPeriodFromDTO(poDTO.getRentalPeriod()));
			model.addAttribute("order", dtoAssembler.purchaseOrderToDTO(po, poDTO.getName(), poDTO.getDescription()));
		} catch (InvalidException e) {
			//TODO:Build and return appropriate message for end user
		}
		
		return "redirect:/dashboard";
	}
}
