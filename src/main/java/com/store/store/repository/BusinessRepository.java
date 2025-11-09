package com.store.store.repository;

import java.util.List;
import java.util.Optional;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import com.store.store.model.Business;
import com.store.store.model.Cliente;
import com.store.store.model.Referidos;
import com.store.store.model.Status;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BusinessRepository {

    Optional<Business> findById(String id);

    List<Business> findAll();

    Business save(Business referidos);

    Boolean deleteAll();

    Boolean deleteById(String id);

    Business edit(Business cliente);
}
