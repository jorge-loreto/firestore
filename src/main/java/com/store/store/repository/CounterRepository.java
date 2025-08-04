package com.store.store.repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import com.store.store.model.Counter;

import org.springframework.stereotype.Repository;

@Repository
public interface CounterRepository extends FirestoreReactiveRepository<Counter> {
}