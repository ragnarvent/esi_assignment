package rentit.com.controllers;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import rentit.com.common.domain.BusinessPeriod;
import rentit.com.inventory.application.PlantCatalogService;
import rentit.com.sales.application.SalesService;
import rentit.com.web.dto.CommonDTOAssembler;
import rentit.com.web.dto.PlantInvEntryAssembler;
import rentit.com.web.dto.PlantInvEntryDTO;
import rentit.com.web.dto.PurchaseOrderAssembler;
import rentit.com.web.dto.PurchaseOrderDTO;

@RestController
@RequestMapping("/api/sales")
public class SalesRestController {
    
	@Autowired
    PlantCatalogService catalogService;
    
    @Autowired
    SalesService salesService;
    
    @Autowired
    PlantInvEntryAssembler entryAssembler;
    
    @Autowired
    PurchaseOrderAssembler poAssembler;
    
    @Autowired
    CommonDTOAssembler commonAssembler;
    
    @RequestMapping(method = RequestMethod.GET, path = "/plants")
    public Collection<PlantInvEntryDTO> findAvailablePlants(
            @RequestParam(name = "name") String plantName,
            @RequestParam(name = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
            ) {
    	return entryAssembler.toResources(catalogService.findAvailablePlants(plantName, BusinessPeriod.of(startDate, endDate)));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/orders/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PurchaseOrderDTO fetchPurchaseOrder(@PathVariable("id") Long id) {
    	return poAssembler.toResource(salesService.fetchPurchaseOrder(id));
    }

    @RequestMapping(method = RequestMethod.POST, path = "/orders")
    public ResponseEntity<PurchaseOrderDTO> createPurchaseOrder(@RequestBody PurchaseOrderDTO partialPODTO) throws URISyntaxException {
        PurchaseOrderDTO newPODTO = poAssembler.toResource(salesService.createAndProcessPO(partialPODTO.getPlantId(), commonAssembler.businessPeriodFromDTO(partialPODTO.getRentalPeriod())));

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(newPODTO.getId().getHref()));

        return new ResponseEntity<PurchaseOrderDTO>(newPODTO, headers, HttpStatus.CREATED);
    }
}