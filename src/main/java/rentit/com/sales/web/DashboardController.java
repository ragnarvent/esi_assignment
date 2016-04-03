package rentit.com.sales.web;

import java.time.LocalDate;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import rentit.com.common.application.dto.BusinessPeriodDTO;
import rentit.com.common.domain.model.BusinessPeriod;
import rentit.com.common.exceptions.InvalidFieldException;
import rentit.com.common.exceptions.PlantNotFoundException;
import rentit.com.common.exceptions.PurchaseOrderNotFoundException;
import rentit.com.inventory.application.dto.PlantInvEntryDTO;
import rentit.com.inventory.application.service.PlantCatalogService;
import rentit.com.sales.application.dto.PurchaseOrderDTO;
import rentit.com.sales.application.service.SalesService;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

	@Autowired
	private PlantCatalogService plantCatalog;

	@Autowired
	private SalesService salesService;
	
	@RequestMapping("catalog/form")
	public String queryForm(Model model) {
		CatalogQueryDTO catalogQuery = new CatalogQueryDTO();
		catalogQuery.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now(), LocalDate.now().plusDays(1)));
		model.addAttribute("catalogQuery", catalogQuery);
		
		return "dashboard/catalog/query-form";
	}
	
	@RequestMapping("catalog/query")
	public String executeQuery(CatalogQueryDTO query, Model model) throws InvalidFieldException {
		Collection<PlantInvEntryDTO> entries = plantCatalog.findAvailablePlants(query.getName(), BusinessPeriod.fromDto(query.getRentalPeriod()));
		model.addAttribute("plants", entries);
		
        PurchaseOrderDTO po = new PurchaseOrderDTO();
        po.setRentalPeriod(query.getRentalPeriod());
		model.addAttribute("po", po);
		
		return "dashboard/catalog/query-result";
	}

	@RequestMapping("/orders")
	public String createPO(Model model, PurchaseOrderDTO purchaseOrderDTO) throws InvalidFieldException, PlantNotFoundException {
		PurchaseOrderDTO po = salesService.createAndProcessPO(purchaseOrderDTO);
		model.addAttribute("po", po );
		
		return "dashboard/catalog/order";
	}
	
	@ExceptionHandler({InvalidFieldException.class, PurchaseOrderNotFoundException.class, PlantNotFoundException.class})
	public ModelAndView handleException(Exception ex) {
	    ModelAndView mav = new ModelAndView();
	    mav.addObject("message", ex.getMessage());
	    mav.setViewName("error");
	    
	    return mav;
	}
}
