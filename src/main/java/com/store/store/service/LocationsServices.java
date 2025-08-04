package com.store.store.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.store.store.model.Category;
import com.store.store.model.CategoryDetails;
import com.store.store.model.Location;
import com.store.store.repository.CategoryDetailsRepository;
import com.store.store.repository.CategoryRepository;
import com.store.store.repository.LocationRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class LocationsServices {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryDetailsRepository categoryDetailsRepository;

    public Mono<Void> saveData(List<Location> jsonLocations) {
        return Flux.fromIterable(jsonLocations)
                .flatMap(location -> {
                    // Save location
                    Mono<Location> locSave = locationRepository.save(location);

                    // Save categories
                    Flux<Category> catSave = Flux.fromIterable(location.getCategories())
                            .map(cat -> {
                                Category c = new Category();
                                c.setId(cat.getId());
                                c.setName(cat.getName());
                                c.setDescription(cat.getDescription());
                                c.setImage(cat.getImage());
                                return c;
                            })
                            .flatMap(categoryRepository::save);

                    // Save category details
                    Flux<CategoryDetails> detSave = Flux.fromIterable(location.getCategories())
                            .map(cat -> {
                                CategoryDetails cd = cat.getCategoryDetails();
                                cd.setId(cd.getId()); // e.g., categoryId:locationId
                                return cd;
                            })
                            .flatMap(categoryDetailsRepository::save);

                    return locSave.thenMany(catSave).thenMany(detSave);
                }).then();
    }

    public Mono<Location> saveLocation(Location location) {
        return locationRepository.save(location);
    }

    public Mono<Void> deleteLocationById(String id) {
        return locationRepository.deleteById(id);
    }

    public Mono<Location> getLocationByName(String name) {
        return locationRepository.findById(name);
    }

    public Mono<Location> getLocationById(String id) {
        return locationRepository.findById(id);
    }

    public Flux<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Mono<Void> deleteLocation(String id) {
        return locationRepository.deleteById(id);
    }

}
