package com.store.store.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.store.store.model.Category;
import com.store.store.model.Counter;
import com.store.store.model.Location;
import com.store.store.model.LocationResponse;
import com.store.store.service.AppMetrics;
import com.store.store.service.CounterService;
import com.store.store.service.LocationsServices;
import com.store.store.service.ReferidosService;

@RestController
@RequestMapping("/locations")
public class LocationController {

    @Autowired
    LocationsServices service;

    @Autowired
    private AppMetrics appMetrics;
    @Autowired
    private CounterService counterService;

    private static final Logger logger = LogManager.getLogger(LocationController.class);

    @GetMapping("/all")
    public List<LocationResponse> getAll() throws Exception {
        counterService.incrementCounter().subscribe(value -> {
            logger.info("Counter incremented to: {}", value);
        }, error -> {
            logger.error("Error incrementing counter: {}", error.getMessage());
        });

        List<LocationResponse> locations = new ArrayList<>();
        try {
            // locations = service.getAllLocations().collectList().block();
            locations = service.getLocationsIteci(null);
            if (locations == null) {
                logger.warn("No locations found");
            }
        } catch (Exception e) {
            logger.error("Error fetching locations: {}", e.getMessage());
            throw e;
        }
        return locations;
    }
    /*
     * ObjectMapper mapper = new ObjectMapper();
     * mapper.registerModule(new JavaTimeModule()); // Support LocalDate
     * 
     * appMetrics.increment();
     * 
     * logger.info("Total requests: {}", appMetrics.getCount());
     * 
     * 
     * InputStream inputStream =
     * getClass().getClassLoader().getResourceAsStream("data.json");
     * 
     * if (inputStream == null) {
     * throw new FileNotFoundException("File not found in resources!");
     * }
     * List<Location> locations = List.of(mapper.readValue(inputStream,
     * Location[].class));
     * 
     * // Print example
     * for (Location loc : locations) {
     * System.out.println("Location: " + loc.getName());
     * for (Category cat : loc.getCategories()) {
     * System.out.println(
     * " - Category: " + cat.getName() + ", Start Date: " +
     * cat.getCategoryDetails().getStartDate());
     * }
     * }
     * return locations;
     * 
     * }
     */

    @GetMapping("/loadData")
    public List<Location> loadData() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Support LocalDate

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data.json");

        if (inputStream == null) {
            throw new FileNotFoundException("File not found in resources!");
        }
        List<LocationResponse> locations = List.of(mapper.readValue(inputStream,
                LocationResponse[].class));

        service.saveData(locations);

        return service.getAllLocations();

    }

    @GetMapping("/v2/all")
    public List<LocationResponse> getAll2(String businessString) throws Exception {
        counterService.incrementCounter().subscribe(value -> {
            logger.info("Counter incremented to: {}", value);
        }, error -> {
            logger.error("Error incrementing counter: {}", error.getMessage());
        });

        List<LocationResponse> locations = new ArrayList<>();
        try {
            // locations = service.getAllLocations().collectList().block();
            locations = service.getLocationsIteci(null);
            if (locations == null) {
                logger.warn("No locations found");
            }
        } catch (Exception e) {
            logger.error("Error fetching locations: {}", e.getMessage());
            throw e;
        }
        return locations;
    }
}