package com.store.store.repository;

import java.util.List;
import java.util.Optional;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import com.store.store.model.Cliente;
import com.store.store.model.Referidos;
import com.store.store.model.Status;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClienteRespository {

    Optional<Cliente> findById(String id);

    List<Cliente> findAll();

    Cliente save(Cliente referidos);

    Boolean deleteAll();

    Boolean deleteById(String id);

    Cliente editCliente(Cliente cliente);
}
