package com.store.store.service;

import com.store.store.model.Business;
import com.store.store.model.Cliente;
import com.store.store.model.Referidos;
import com.store.store.model.Status;
import com.store.store.repository.BusinessRepository;
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
public class BusinessService {

    private static final Logger logger = LogManager.getLogger(BusinessService.class);

    @Autowired
    private BusinessRepository repository;

    public String saveBusiness(Business business) throws Exception {
        business.setCreationDate(parseDate());
        logger.info("business to be saved: {}", business);
        if (business == null) {
            logger.error("Business is null, cannot save.");
            throw new Exception("Referido cannot be null");
        }

        repository.save(business);

        logger.info("Business saved with ID: {}", business.getId());
        return "Guardado exitosamente en: " + business.getId();
    }

    public ResponseEntity<Business> getById(@PathVariable String id) {
        return repository.findById(id)
                .map(referido -> ResponseEntity.ok(referido))
                .orElse(ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body(null));
    }

    public List<Business> obtenerTodos() {
        return repository.findAll();
    }

    public Boolean eliminar(String id) {
        return repository.deleteById(id);
    }

    private String parseDate() {
        try {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("yyyy-MM-dd hh:mm:ss a")
                    .toFormatter(Locale.ENGLISH);
            LocalDateTime now = LocalDateTime.now();
            String cleaned = now.format(formatter);
            return cleaned.replace(".", "");
        } catch (Exception e) {
            return null;
        }
    }

}

// Aquí puedes agregar más métodos relacionados con Referidos si es necesary