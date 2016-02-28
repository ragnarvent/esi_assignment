package rentit.com.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import rentit.com.common.RentitException;
import rentit.com.inventory.application.PlantCatalogService;
import rentit.com.inventory.domain.PlantInvEntry;
import rentit.com.sales.application.SalesService;
import rentit.com.sales.domain.PurchaseOrder;
import rentit.com.web.dto.BusinessPeriodDTO;
import rentit.com.web.dto.CatalogQueryDTO;
import rentit.com.web.dto.DTOAssembler;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

	@Autowired
	private PlantCatalogService plantCatalog;

	@Autowired
	private SalesService salesService;
	
	@Autowired
	private DTOAssembler dtoAssembler;
	
	@RequestMapping("catalog/form")
	String queryForm(Model model) {
		model.addAttribute("catalogQuery", new CatalogQueryDTO());
		return "dashboard/catalog/query-form";
	}
	
	@RequestMapping("catalog/query")
	String executeQuery(CatalogQueryDTO query, Model model) {
		Collection<PlantInvEntry> entries = plantCatalog.findAvailablePlants(query.getName(), dtoAssembler.businessPeriodFromDTO(query.getRentalPeriod()));
		model.addAttribute("plants", dtoAssembler.plantEntriesToDTO(entries));
		model.addAttribute("rentalPeriod", query.getRentalPeriod());
		
		return "dashboard/catalog/query-result";
	}

	@RequestMapping("/orders")
	String createPO(String plantId, String plantName, String plantDescription, BusinessPeriodDTO rentalPeriod, Model model) {
		try {
			PurchaseOrder po = salesService.createAndProcessPO(Long.valueOf(plantId), dtoAssembler.businessPeriodFromDTO(rentalPeriod));
			model.addAttribute("po", dtoAssembler.purchaseOrderToDTO(po, plantName, plantDescription));
		} catch (RentitException e) {
			//TODO: Build and return appropriate message for end user
		}
		
		return "dashboard/catalog/order";
	}
}
