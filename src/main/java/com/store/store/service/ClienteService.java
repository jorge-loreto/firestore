package com.store.store.service;

import com.store.store.model.Cliente;
import com.store.store.model.Referidos;
import com.store.store.model.Status;
import com.store.store.repository.ClienteRespository;
import com.store.store.repository.ReferidosRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

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
public class ClienteService {

    private static final Logger logger = LogManager.getLogger(ClienteService.class);

    @Autowired
    private ClienteRespository repository;

    public String guardarCliente(Cliente referido) throws Exception {

        logger.info("Referido to be saved: {}", referido);
        if (referido == null) {
            logger.error("Referido is null, cannot save.");
            throw new Exception("Referido cannot be null");
        }

        repository.save(referido);

        logger.info("Referido saved with ID: {}", referido.getId());
        return "Guardado exitosamente en: " + referido.getId();
    }

    public ResponseEntity<Cliente> getById(@PathVariable String id) {
        return repository.findById(id)
                .map(referido -> ResponseEntity.ok(referido))
                .orElse(ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body(null));
    }

    public List<Cliente> obtenerTodos() {
        return repository.findAll();
    }

    public Boolean eliminar(String id) {
        return repository.deleteById(id);
    }

    private LocalDateTime parseDate(String dateStr, DateTimeFormatter formatter) {
        try {
            if (dateStr == null || dateStr.isEmpty())
                return null;
            String cleaned = dateStr.trim().replace(".", ""); // remove dots from "p.m."
            return LocalDateTime.parse(cleaned, formatter);
        } catch (Exception e) {
            return null;
        }
    }

}

// Aquí puedes agregar más métodos relacionados con Referidos si es necesary