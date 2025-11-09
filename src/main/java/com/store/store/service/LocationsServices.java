package com.store.store.service;

import java.util.List;
import java.util.Optional; // ADD THIS
import java.util.stream.Collectors; // Potentially needed for collecting Iterables

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.store.store.model.Category;
import com.store.store.model.CategoryDetails;
import com.store.store.model.CategoryResponse;
import com.store.store.model.Location;
import com.store.store.model.LocationResponse;
import com.store.store.repository.CategoryDetailsRepository;
import com.store.store.repository.CategoryRepository;
import com.store.store.repository.LocationRepository;
import com.store.store.utils.LocationMapper;

// REMOVE THESE:
// import reactor.core.publisher.Flux;
// import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList; // Potentially needed
import java.util.Arrays;

@Service
public class LocationsServices {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryDetailsRepository categoryDetailsRepository;

    // --- Blocking Methods ---

    // Mono<Void> saveData(List<LocationResponse> jsonLocations) becomes:
    public void saveData(List<LocationResponse> jsonLocations) {
        for (LocationResponse locationResponse : jsonLocations) {
            // Save location
            locationRepository.save(LocationMapper.toLocation(locationResponse));

            // Save categories
            for (CategoryResponse catResponse : locationResponse.getCategories()) {
                Category c = new Category();
                c.setId(catResponse.getId());
                c.setName(catResponse.getName());
                c.setDescription(catResponse.getDescription());
                c.setImage(catResponse.getImage());
                categoryRepository.save(c);
            }

            // Save category details
            for (CategoryResponse catResponse : locationResponse.getCategories()) {
                CategoryDetails cd = catResponse.getCategoryDetails();
                cd.setId(cd.getId()); // e.g., categoryId:locationId
                categoryDetailsRepository.save(cd);
            }
        }
    }

    // Mono<Location> saveLocation(Location location) becomes:
    public Location saveLocation(Location location) {
        return locationRepository.save(location);
    }

    // Mono<Void> deleteLocationById(String id) becomes:
    public void deleteLocationById(String id) {
        locationRepository.deleteById(id);
    }

    // Mono<Location> getLocationByName(String name) becomes:
    public Optional<Location> getLocationByName(String name) { // Use Optional for findById
        return locationRepository.findById(name);
    }

    // Mono<Location> getLocationById(String id) becomes:
    public Optional<Location> getLocationById(String id) {
        return locationRepository.findById(id);
    }

    // Flux<Location> getAllLocations() becomes:
    public List<Location> getAllLocations() { // Or Iterable<Location>
        List<Location> locations = new ArrayList<>();
        locationRepository.findAll().forEach(locations::add); // Iterating over Iterable
        return locations;
    }

    // Mono<Void> deleteLocation(String id) becomes:
    public void deleteLocation(String id) {
        locationRepository.deleteById(id);
    }

    // This method will require significant refactoring due to reactive chaining
    public LocationResponse getLocationWithDetails(String locationId) {
        Optional<Location> locationOpt = locationRepository.findById(locationId);
        if (locationOpt.isEmpty()) {
            return null; // Or throw an exception
        }
        Location location = locationOpt.get();

        List<CategoryResponse> categoryResponses = new ArrayList<>();
        for (String categoryId : location.getCategoriesList()) {
            Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
            if (categoryOpt.isPresent()) {
                Category category = categoryOpt.get();
                Optional<CategoryDetails> detailsOpt = categoryDetailsRepository.findById(category.getId());
                detailsOpt.ifPresent(details -> categoryResponses.add(new CategoryResponse(category, details)));
            }
        }
        // Assuming your buildLocationResponse works with List<CategoryResponse>
        return buildLocationResponse(location, categoryResponses);
    }

    // NEW: Get all locations with details - this will be complex to convert fully
    // blocking
    public List<LocationResponse> getLocationsIteci(String locs) {
        List<LocationResponse> allResponses = new ArrayList<>();
        if (locs == null || locs.isEmpty()) {
            locs = "1,2,3,abc123";
        }
        List<String> locationIds = Arrays.asList(locs.split(","));

        Iterable<Location> locations = locationRepository.findAllById(locationIds);

        for (Location location : locations) {
            List<CategoryResponse> categoryResponses = new ArrayList<>();
            for (String categoryId : location.getCategoriesList()) {
                Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
                if (categoryOpt.isPresent()) {
                    Category category = categoryOpt.get();
                    // Note: This ID mapping 'category.getId() + ":" + location.getId()' should be
                    // handled carefully
                    Optional<CategoryDetails> detailsOpt = categoryDetailsRepository
                            .findById(category.getId() + ":" + location.getId());

                    detailsOpt.ifPresent(details -> {
                        if (category.getImage().equals("PREPA-EN-LINEA.png")) {
                            details.setStartDate(generateCurrentDate());
                        }
                        categoryResponses.add(new CategoryResponse(category, details));
                    });
                }
            }
            // Sort categories by startDate before collecting
            categoryResponses.sort((c1, c2) -> c1.getCategoryDetails().getStartDate()
                    .compareTo(c2.getCategoryDetails().getStartDate()));
            allResponses.add(buildLocationResponse(location, categoryResponses));
        }
        return allResponses;
    }

    // Helper to map Location + Categories to LocationResponse (unchanged if it
    // takes List)
    private LocationResponse buildLocationResponse(Location location, List<CategoryResponse> categories) {
        // ... (your existing implementation) ...
        LocationResponse response = new LocationResponse();
        response.setId(location.getId());
        response.setName(location.getName());
        response.setDescription(location.getDescription());
        response.setAddress(location.getAddress());
        response.setCity(location.getCity());
        response.setState(location.getState());
        response.setZip(location.getZip());
        response.setPhone(location.getPhone());
        response.setTarjetaBancomer(location.getTarjetaBancomer());
        response.setOxxo(location.getOxxo());
        response.setCategoriesList(location.getCategoriesList());
        response.setCategories(categories);
        return response;
    }

    private String generateCurrentDate() {
        // ... (your existing implementation) ...
        LocalDate today = LocalDate.now().plusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = today.format(formatter);
        return String.valueOf(formattedDate);
    }
}
