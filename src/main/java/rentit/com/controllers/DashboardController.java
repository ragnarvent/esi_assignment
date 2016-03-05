package rentit.com.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import rentit.com.common.RentitException;
import rentit.com.inventory.application.PlantCatalogService;
import rentit.com.inventory.domain.PlantInvEntry;
import rentit.com.sales.application.SalesService;
import rentit.com.sales.domain.PurchaseOrder;
import rentit.com.web.dto.BusinessPeriodDTO;
import rentit.com.web.dto.CatalogQueryDTO;
import rentit.com.web.dto.CommonDTOAssembler;
import rentit.com.web.dto.PlantInvEntryAssembler;
import rentit.com.web.dto.PurchaseOrderAssembler;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

	@Autowired
	private PlantCatalogService plantCatalog;

	@Autowired
	private SalesService salesService;
	
	@Autowired
	private CommonDTOAssembler dtoAssembler;
	
	@Autowired
	PlantInvEntryAssembler entryAssembler;
	
	@Autowired
	PurchaseOrderAssembler poAssembler;
	
	@RequestMapping("catalog/form")
	@ExceptionHandler({RentitException.class})
	String queryForm(Model model) {
		CatalogQueryDTO catalogQuery = new CatalogQueryDTO();
		catalogQuery.setRentalPeriod(BusinessPeriodDTO.of("yyyy-MM-dd", "yyyy-MM-dd"));
		model.addAttribute("catalogQuery", catalogQuery);
		
		return "dashboard/catalog/query-form";
	}
	
	@RequestMapping("catalog/query")
	@ExceptionHandler({RentitException.class})
	String executeQuery(CatalogQueryDTO query, Model model) {
		Collection<PlantInvEntry> entries = plantCatalog.findAvailablePlants(query.getName(), dtoAssembler.businessPeriodFromDTO(query.getRentalPeriod()));
		model.addAttribute("plants", entryAssembler.toResources(entries));
		model.addAttribute("rentalPeriod", query.getRentalPeriod());
		
		return "dashboard/catalog/query-result";
	}

	@RequestMapping("/orders")
	@ExceptionHandler({RentitException.class})
	String createPO(String plantId, String plantName, String plantDescription, BusinessPeriodDTO rentalPeriod, Model model) {
		PurchaseOrder po = salesService.createAndProcessPO(Long.valueOf(plantId), dtoAssembler.businessPeriodFromDTO(rentalPeriod));
		model.addAttribute("po", poAssembler.toResource(po) );
		
		return "dashboard/catalog/order";
	}
}
