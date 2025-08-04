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
    public List<Location> getAll() throws Exception {
        // String json = service.getAllCategories().toString(); // get raw JSON
        // Convert JSON to List<Category>
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Support LocalDate

        appMetrics.increment();

        logger.info("Total requests: {}", appMetrics.getCount());

        // Load from file
        /*
         * File file = new File("data.json");
         * List<Location> locations = List.of(mapper.readValue(file, Location[].class));
         */
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data.json");

        if (inputStream == null) {
            throw new FileNotFoundException("File not found in resources!");
        }
        List<Location> locations = List.of(mapper.readValue(inputStream, Location[].class));

        // Print example
        for (Location loc : locations) {
            System.out.println("Location: " + loc.getName());
            for (Category cat : loc.getCategories()) {
                System.out.println(
                        " - Category: " + cat.getName() + ", Start Date: " + cat.getCategoryDetails().getStartDate());
            }
        }
        return locations;

    }

    @GetMapping("/loadData")
    public List<Location> loadData() throws Exception {
        /*
         * ObjectMapper mapper = new ObjectMapper();
         * mapper.registerModule(new JavaTimeModule()); // Support LocalDate
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
         * 
         * service.saveData(locations).block();
         * return service.getAllLocations().collectList().block();
         */
        counterService.incrementCounter().subscribe(value -> {
            logger.info("Counter incremented to: {}", value);
        }, error -> {
            logger.error("Error incrementing counter: {}", error.getMessage());
        });
        return null;

    }
}