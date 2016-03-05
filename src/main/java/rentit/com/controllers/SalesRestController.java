package rentit.com.controllers;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import rentit.com.inventory.application.PlantCatalogService;
import rentit.com.sales.application.SalesService;

import rentit.com.web.dto.PlantInvEntryDTO;
import rentit.com.web.dto.PurchaseOrderDTO;

@RestController
@RequestMapping("/api/sales")
public class SalesRestController {
    @Autowired
    PlantCatalogService catalogService;
    @Autowired
    SalesService salesService;

    @RequestMapping(method = RequestMethod.GET, path = "/plants")
    public List<PlantInvEntryDTO> findAvailablePlants(
            @RequestParam(name = "name") String plantName,
            @RequestParam(name = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
            ) {
        // TODO: Complete this part
    }

    @RequestMapping(method = RequestMethod.GET, path = "/orders/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PurchaseOrderDTO fetchPurchaseOrder(@PathVariable("id") Long id) {
        // TODO: Complete this part
    }

    @RequestMapping(method = RequestMethod.POST, path = "/orders")
    public ResponseEntity<PurchaseOrderDTO> createPurchaseOrder(@RequestBody PurchaseOrderDTO partialPODTO) {
        PurchaseOrderDTO newlyCreatePODTO = ...
        // TODO: Complete this part

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(newlyCreatePODTO.getId().getHref()));

        return new ResponseEntity<PurchaseOrderDTO>(newlyCreatePODTO, headers, HttpStatus.CREATED);
    }
}