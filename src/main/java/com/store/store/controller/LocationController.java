package com.store.store.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.store.store.model.Business;
import com.store.store.model.Category;
import com.store.store.model.Counter;
import com.store.store.model.Location;
import com.store.store.model.LocationResponse;
import com.store.store.service.AppMetrics;
import com.store.store.service.BusinessService;
import com.store.store.service.CounterService;
import com.store.store.service.LocationsServices;
import com.store.store.service.ReferidosService;
import com.store.store.utils.LocationMapper;

@RestController
@RequestMapping("/locations")
public class LocationController {

    @Autowired
    LocationsServices service;

    @Autowired
    private AppMetrics appMetrics;
    @Autowired
    private CounterService counterService;

    @Autowired
    private BusinessService businessService;

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

    @PostMapping("/locations/{id}")
    public Location create(@PathVariable String id, @RequestBody Location location) {
        logger.info("Creating location for business id: {}", id);
        Location loc = service.saveLocation(location);
        Business business = businessService.getBusinessById(id);
        logger.info("getting business id: {}", business);
        if (business != null) {
            List<String> locationIds = business.getLocationIds();
            if (locationIds == null) {
                locationIds = new ArrayList<>();
            }

            locationIds.add(loc.getId());
            // Update the list of location IDs in the
            List<Location> allLocations = service.getAllLocations();
            List<String> validLocationIds = allLocations.stream()
                    .map(Location::getId)
                    .toList();

            List<String> validatedLocationIds = business.getLocationIds()
                    .stream()
                    .filter(locId -> {
                        logger.info("Existing location ID: {}", locId);
                        boolean exists = validLocationIds.contains(locId);
                        if (exists) {
                            logger.info("Location exists for ID: {}", locId);
                        } else {
                            logger.warn("Location does not exist for ID: {}", locId);
                        }
                        return exists;
                    })
                    .toList();
            business.setLocationIds(validatedLocationIds);
            try {
                businessService.saveBusiness(business);
            } catch (Exception e) {
                logger.error("Error saving business with new location: {}", e.getMessage());
            }
        } else {
            logger.warn("Business not found for id: {}", id);
        }

        return loc;
    }

    @GetMapping("/business/{id}")
    public ResponseEntity<List<LocationResponse>> getLocationsByBusiness(@PathVariable String id) throws Exception {
        logger.info("Looking for location for business id: {}", id);
        String idBusiness = id;
        appMetrics.increment();
        logger.info("Total requests: {}", appMetrics.getCount());
        List<LocationResponse> locations = new ArrayList<>();
        try {
            Business business = businessService.getBusinessById(idBusiness);
            if (business == null) {
                logger.warn("Business not found for idBusiness: {}", idBusiness);
                return ResponseEntity.ok(locations);
            }
            List<String> locIds = business.getLocationIds();
            logger.info("locations to be lookup: {}", locIds);
            if (locIds == null || locIds.isEmpty()) {
                logger.warn("No location IDs found for business id: {}", idBusiness);
                return ResponseEntity.ok(locations);
            }
            String locatuiStringons = String.join(",", locIds);
            logger.info("Location IDs string to lookup: {}", locatuiStringons);
            System.out.println("Location IDs string to lookup: " + locatuiStringons);
            locations = service.getLocationsIteci(locatuiStringons);
            if (locations == null || locations.isEmpty()) {
                logger.warn("No locations found for business id: {}", idBusiness);
            }
        } catch (Exception e) {
            logger.error("Error fetching locations for business id {}: {}", idBusiness, e.getMessage());
            throw e;
        }
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/unassigned")
    public ResponseEntity<List<LocationResponse>> getUnnassignedLocations() throws Exception {
        logger.info("Looking for location unassigned");
        List<Location> unassignedLocations = new ArrayList<>();
        try {
            List<Location> allLocations = service.getAllLocations();

            // 1️⃣ Get ALL assigned location IDs across all businesses
            Set<String> assignedLocationIds = businessService.obtenerTodos().stream()
                    .map(Business::getLocationIds)
                    .filter(Objects::nonNull)
                    .flatMap(List::stream)
                    .collect(Collectors.toSet());

            // 2️⃣ Keep only locations NOT assigned to any business
            unassignedLocations = allLocations.stream()
                    .filter(loc -> !assignedLocationIds.contains(loc.getId()))
                    .toList();

        } catch (Exception e) {
            logger.error("Error fetching locations unnassigned: {}", e.getMessage());
            throw e;
        }
        logger.info("Found {} unassigned locations", unassignedLocations.size());
        logger.info("Unassigned locations details: {}", unassignedLocations);
        return ResponseEntity.ok(unassignedLocations.stream()
                .map(LocationMapper::toLocationResponse)
                .collect(Collectors.toList()));
    }

    @PutMapping("/{locationId}/assign/{businessId}")
    public ResponseEntity<Location> updateBizWithLocation(@PathVariable String locationId,
            @PathVariable String businessId) {
        logger.info("Creating location for business id: {}", businessId);
        logger.info("Assigning location id: {} to business id: {}", locationId, businessId);
        Business business = businessService.getBusinessById(businessId);
        logger.info("getting business id: {}", business);
        if (business != null) {
            List<String> locationIds = business.getLocationIds();
            if (locationIds == null) {
                locationIds = new ArrayList<>();
            }

            locationIds.add(locationId);
            business.setLocationIds(locationIds);
            try {
                businessService.updateBusiness(business);
            } catch (Exception e) {
                logger.error("Error saving business with new location: {}", e.getMessage());
            }
        } else {
            logger.warn("Business not found for id: {}", businessId);
        }

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{locationId}/unassign/{businessId}")
    public ResponseEntity<Void> unassignLocationFromBusiness(
            @PathVariable String locationId,
            @PathVariable String businessId) {

        logger.info("Unassigning location [{}] from business [{}]", locationId, businessId);

        Business business = businessService.getBusinessById(businessId);
        if (business == null) {
            logger.warn("Business not found for id: {}", businessId);
            return ResponseEntity.notFound().build();
        }

        List<String> locationIds = business.getLocationIds();
        if (locationIds == null || locationIds.isEmpty()) {
            logger.warn("Business [{}] has no locations assigned", businessId);
            return ResponseEntity.ok().build();
        }

        boolean removed = locationIds.remove(locationId);

        if (!removed) {
            logger.warn("Location [{}] not found in business [{}]", locationId, businessId);
            return ResponseEntity.ok().build();
        }

        business.setLocationIds(locationIds);

        try {
            businessService.updateBusiness(business);
            logger.info("Location [{}] successfully removed from business [{}]", locationId, businessId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error updating business [{}]: {}", businessId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Location> updateLocation(
            @PathVariable String id,
            @RequestBody Location location) {

        logger.info("Updating location for id: {}", id);
        logger.info("Updating location for id: {}", location);
        Optional<Location> existingOpt = service.getLocationById(id);
        if (existingOpt.isEmpty()) {
            logger.warn("Location not found for id: {}", id);
            return ResponseEntity.notFound().build();
        }

        Location existing = existingOpt.get();

        Set<String> existingCats = safeSet(existing.getCategoriesList());
        Set<String> incomingCats = safeSet(location.getCategoriesList());

        if (existingCats.equals(incomingCats)) {
            logger.info("Categories unchanged");
        } else {
            logger.info("Categories changed from {} to {}", existingCats, incomingCats);
        }

        location.setId(id);
        logger.info("Final location to update: {}", location);
        boolean updated = service.updateLocation(location);

        if (!updated) {
            logger.error("Failed to update location with id: {}", id);
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(location);
    }

    private Set<String> safeSet(List<String> list) {
        return list == null ? Set.of() : Set.copyOf(list);
    }

}