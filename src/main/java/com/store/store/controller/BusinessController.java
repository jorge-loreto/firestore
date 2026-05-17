package com.store.store.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.store.store.model.Business;
import com.store.store.model.Category;
import com.store.store.model.Cliente;
import com.store.store.model.Counter;
import com.store.store.model.Location;
import com.store.store.model.LocationResponse;
import com.store.store.service.AppMetrics;
import com.store.store.service.BusinessService;
import com.store.store.service.CounterService;
import com.store.store.service.LocationsServices;
import com.store.store.service.ReferidosService;
import com.store.store.utils.ResponseWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/business")
public class BusinessController {

    @Autowired
    BusinessService service;

    @Autowired
    CounterService counterService;

    @Autowired
    LocationsServices locationsService;

    @Autowired
    private AppMetrics appMetrics;

    private static final Logger logger = LogManager.getLogger(LocationController.class);

    @GetMapping("/info")
    public List<LocationResponse> getAll(String idBusiness) throws Exception {
        counterService.incrementCounter().subscribe(value -> {
            logger.info("Counter incremented to: {}", value);
        }, error -> {
            logger.error("Error incrementing counter: {}", error.getMessage());
        });

        List<LocationResponse> locations;
        ResponseEntity<Business> businessResponse = service.getById(idBusiness);
        Business business = businessResponse != null ? businessResponse.getBody() : null;
        if (business == null) {
            logger.warn("Business not found for idBusiness: {}", idBusiness);
            return new ArrayList<>();
        }
        String locs = business.getLocationIds().stream().reduce("", (a, b) -> a + "," + b);
        try {
            // locations = service.getAllLocations().collectList().block();
            locations = locationsService.getLocationsIteci(locs);
            if (locations == null) {
                logger.warn("No locations found");
            }
        } catch (Exception e) {
            logger.error("Error fetching locations: {}", e.getMessage());
            throw e;
        }
        return locations;
    }

    @GetMapping("/all")
    public List<Business> getAll() {
        return service.obtenerTodos();
    }

    @PutMapping("/{id}")
    public Map<String, Object> editBusiness(@PathVariable String id, @RequestBody Business businessEntity) {
        try {
            businessEntity.setId(id);
            Business updatedBusiness = service.getBusinessById(id);
            if (updatedBusiness == null) {
                return ResponseWrapper.error(new Exception("Business not found"),
                        "Business with id: " + id + " not found", businessEntity);
            }
            updatedBusiness.setCelular(businessEntity.getCelular());
            updatedBusiness.setNombre(businessEntity.getNombre());
            updatedBusiness.setNotas(businessEntity.getNotas());
            logger.info("Updating Business: {}", updatedBusiness);
            service.saveBusiness(updatedBusiness);
            return ResponseWrapper.successNoMessages();
        } catch (Exception e) {
            logger.error("Error editing Business: {}", e.getMessage());
            return ResponseWrapper.error(e, "Error editing Business with id: " + id, businessEntity);
        }
    }

    @PostMapping("/save")
    public Map<String, Object> guardar(@RequestBody Business requestJson) {
        logger.info("Received JSON: {}", requestJson);
        try {
            // Referidos refer = objectMapper.convertValue(requestJson, Referidos.class);
            logger.info("refer object Referidos: {}", requestJson);
            service.saveBusiness(requestJson);
            logger.info("Parsed JSON: {}", requestJson);
            return requestJson != null
                    ? ResponseWrapper.successNoMessages()
                    : ResponseWrapper.error(new Exception("Parsed JSON is null"), "Error in method receiveReferido",
                            requestJson.toString());
        } catch (JsonProcessingException e) {
            logger.error("Error processing JSON: {}", e.getMessage());
            return ResponseWrapper.error(e, "Error in method receiveReferido", requestJson.toString());
        } catch (Exception e) {
            logger.error("Error saving user: {}", e.getMessage());
            return ResponseWrapper.error(e, "Error in saving receiveReferido", requestJson.toString());
        }
    }

}