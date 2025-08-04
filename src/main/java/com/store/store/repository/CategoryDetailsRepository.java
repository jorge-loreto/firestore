package com.store.store.repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import com.store.store.model.CategoryDetails;

public interface CategoryDetailsRepository extends FirestoreReactiveRepository<CategoryDetails> {
}
