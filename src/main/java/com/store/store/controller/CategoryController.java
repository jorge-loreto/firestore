
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
import com.store.store.model.CategoryDetails;
import com.store.store.model.Counter;
import com.store.store.model.Location;
import com.store.store.model.LocationResponse;
import com.store.store.service.AppMetrics;
import com.store.store.service.BusinessService;
import com.store.store.service.CategoryService;
import com.store.store.service.CounterService;
import com.store.store.service.LocationsServices;
import com.store.store.service.ReferidosService;
import com.store.store.utils.LocationMapper;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    CategoryService service;

    @Autowired
    private BusinessService businessService;

    final Logger logger = LogManager.getLogger(CategoryController.class);

    @PostMapping("/by-ids")
    public ResponseEntity<List<Category>> getIds(@RequestBody String[] ids) {
        logger.info("Fetching categories for IDs: {}", (Object) ids);
        List<Category> categories = new ArrayList<>();
        for (String id : ids) {
            var category = service.getCategoryById(id);
            if (category != null) {
                categories.add(category.get());
            }
        }
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updatedCategory(@PathVariable String id, @RequestBody Category category) {
        logger.info("Category to be update: {}", category);
        Optional<Category> updatedCategory = service.getCategoryById(id);
        if (updatedCategory == null) {
            return ResponseEntity.notFound().build();
        }
        category.setId(id);
        try {
            service.saveCategory(category);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error("Error updating category with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(500).body("Error updating category");
        }
        return ResponseEntity.ok("Category updated successfully");
    }

    @GetMapping("/details/{locationId}/{categoryId}")
    public ResponseEntity<CategoryDetails> getCategoryDetailsById(@PathVariable String locationId,
            @PathVariable String categoryId) {
        logger.info("Fetching category details for locationId: {} and categoryId: {}", locationId, categoryId);
        Optional<CategoryDetails> categoryDetails = service.getCategoryDetailsById(categoryId + ":" + locationId);
        if (categoryDetails.isPresent()) {
            logger.info("Category details found: {}", categoryDetails.get());
            return ResponseEntity.ok(categoryDetails.get());
        } else {
            logger.warn("Category details not found for ID: {}", categoryId + ":" + locationId);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/details/{locationId}/{categoryId}")
    public ResponseEntity<CategoryDetails> updateCategoryDetailsById(@PathVariable String locationId,
            @PathVariable String categoryId, @RequestBody CategoryDetails categoryDetailsData) {
        logger.info("Updating category details for locationId: {} and categoryId: {}", locationId, categoryId);
        categoryDetailsData.setId(categoryId + ":" + locationId);
        try {
            service.updateCategoryDetailsById(categoryDetailsData);
            logger.info("Category details updated successfully for ID: {}", categoryId + ":" + locationId);

            return ResponseEntity.ok(categoryDetailsData);
        } catch (Exception e) {
            logger.error("Error updating category details with ID {}: {}", categoryId + ":" + locationId,
                    e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Category>> getAll() {
        logger.info("Fetching ALL the categories");
        List<Category> categories = service.getAllCategories();
        return ResponseEntity.ok(categories);
    }

}
