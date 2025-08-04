package com.store.store.repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import com.store.store.model.Category;

public interface CategoryRepository extends FirestoreReactiveRepository<Category> {

}
