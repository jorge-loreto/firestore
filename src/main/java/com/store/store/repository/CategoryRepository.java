package com.store.store.repository;

import java.util.List;
import java.util.Optional;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import com.store.store.model.Category;

public interface CategoryRepository {

    boolean save(Category category);

    void deleteById(String id);

    void deleteAll();

    long count();

    boolean findById2(String id);

    Optional<Category> findById(String id);

    List<Category> findAll();

}
