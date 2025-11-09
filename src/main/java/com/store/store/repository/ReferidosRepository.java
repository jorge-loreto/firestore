package com.store.store.repository;

import java.util.List;
import java.util.Optional;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import com.store.store.model.Referidos;
import com.store.store.model.Status;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReferidosRepository {
    // 🔹 This must be declared so Spring can generate the query
    List<Referidos> findByStatus(Status status);

    Optional<Referidos> findById(String id);

    List<Referidos> findAll();

    Referidos save(Referidos referidos);

    Boolean deleteAll();

    Boolean deleteById(String id);
}
