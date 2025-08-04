package com.store.store.service;

import com.google.common.base.Optional;
import com.store.store.model.Referidos;
import com.store.store.repository.ReferidosRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class ReferidosService {

    private static final Logger logger = LogManager.getLogger(ReferidosService.class);

    @Autowired
    private ReferidosRepository referidosRepository;

    public String guardarReferido(Referidos referido) throws Exception {

        logger.info("Referido to be saved: {}", referido);
        if (referido == null) {
            logger.error("Referido is null, cannot save.");
            throw new Exception("Referido cannot be null");
        }

        referidosRepository.save(referido)
                .doOnSuccess(saved -> logger.info("Referido  saved SUCCESSFULLY: {}",
                        referido))
                .doOnError(e -> logger.error("Error al guardar: " + e.getMessage()))
                .block();

        logger.info("Referido saved with ID: {}", referido.getId());
        return "Guardado exitosamente en: " + referido.getId();
    }

    @GetMapping("/referidos/{id}")
    public Mono<ResponseEntity<Referidos>> getById(@PathVariable String id) {
        return referidosRepository.findById(id)
                .map(referido -> ResponseEntity.ok(referido))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    public Mono<Referidos> obtenerReferidoPorId(String id) {
        return referidosRepository.findById(id);
    }

    public Flux<Referidos> obtenerTodosReferidos() {
        return referidosRepository.findAll();
    }

}

// Aquí puedes agregar más métodos relacionados con Referidos si es necesary