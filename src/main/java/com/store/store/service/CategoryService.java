
package com.store.store.service;

import java.util.List;
import java.util.Optional; // ADD THIS
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors; // Potentially needed for collecting Iterables

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public class CategoryService {
    public final Logger logger = LogManager.getLogger(CategoryService.class);

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryDetailsRepository categoryDetailsRepository;

    // --- Blocking Methods ---

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(String id) {
        return categoryRepository.findById(id);
    }

    public String saveCategory(Category category) throws Exception {
        if (category == null) {
            logger.error("Category is null, cannot save.");
            throw new Exception("Category cannot be null");
        }

        categoryRepository.save(category);

        logger.info("Category saved with ID: {}", category.getId());
        return "Guardado exitosamente en: " + category.getId();
    }

    public List<Category> getCategoryByIds(String idsString) {
        return categoryRepository.findAll().stream()
                .filter(category -> Arrays.asList(idsString.split(",")).contains(category.getId()))
                .collect(Collectors.toList());
    }

    public Optional<CategoryDetails> getCategoryDetailsById(String id) {
        logger.info("Fetching category details with ID: {}", id);
        return categoryDetailsRepository.findById(id);
    }

    public boolean updateCategoryDetailsById(CategoryDetails categoryDetails) {
        logger.info("Updating category details with ID: {}", categoryDetails.getId());
        return categoryDetailsRepository.updateCategoryDetails(categoryDetails);
    }

}
