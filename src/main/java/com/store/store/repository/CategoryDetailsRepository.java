
package com.store.store.repository;

import java.util.Optional;

import com.store.store.model.Category;
import com.store.store.model.CategoryDetails;

import reactor.core.publisher.Mono;

// Change this interface extension
public interface CategoryDetailsRepository {

    Iterable<Category> findAll();

    Boolean save(Category category);

    void deleteById(String id);

    void deleteAll();

    long count();

    boolean existsById(String id);

    Iterable<Category> findAllById(Iterable<String> ids);

    Iterable<Category> saveAll(Iterable<Category> categories);

    void delete(Category category);

    void deleteAll(Iterable<? extends Category> categories);

    void deleteAllCategories();

    boolean updateCategory(Category category);

    boolean updateCategoryDetails(CategoryDetails categoryDetails);

    Mono<CategoryDetails> save(CategoryDetails categoryDetails);

    Optional<CategoryDetails> findById(String id);
}
