package com.store.store.repository;

import com.store.store.model.Referidos;
import com.store.store.model.Status;
import reactor.core.publisher.Flux;

public interface ReferidosRepositoryCustom {
    Flux<Referidos> findByStatusOrdered(Status status);
}
