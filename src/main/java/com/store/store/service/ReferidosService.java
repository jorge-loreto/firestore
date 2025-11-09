package com.store.store.service;

import com.store.store.model.Referidos;
import com.store.store.model.Status;
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

        referidosRepository.save(referido);

        logger.info("Referido saved with ID: {}", referido.getId());
        return "Guardado exitosamente en: " + referido.getId();
    }

    public ResponseEntity<Referidos> getById(@PathVariable String id) {
        return referidosRepository.findById(id)
                .map(referido -> ResponseEntity.ok(referido))
                .orElse(ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body(null));
    }

    public List<Referidos> obtenerTodosReferidos() {
        return referidosRepository.findAll();
    }

    public Boolean eliminarReferido(String id) {
        return referidosRepository.deleteById(id);
    }

    public List<Referidos> obtenerReferidosPorStatus(Status status) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("d/M/yyyy, h:mm:ss a")
                .toFormatter(new Locale("es", "MX"));

        List<Referidos> referidos = referidosRepository.findByStatus(status);

        referidos.sort(Comparator
                .comparing(Referidos::getPlantel, Comparator.nullsLast(String::compareToIgnoreCase))
                .thenComparing(
                        r -> parseDate(r.getCreationDate(), formatter),
                        Comparator.nullsLast(LocalDateTime::compareTo)));

        return referidos;
    }

    public Referidos actualizarStatus(Referidos referidos) {
        // Find the existing referido by ID
        Optional<Referidos> existingOpt = referidosRepository.findById(referidos.getId());

        if (existingOpt.isEmpty()) {
            throw new NoSuchElementException("Referido not found with ID: " + referidos.getId());
        }

        Referidos existente = existingOpt.get();

        // Update fields
        existente.setStatus(referidos.getStatus());
        existente.setNotas(referidos.getNotas());
        existente.setInscripcion(referidos.getInscripcion());
        existente.setPremio(referidos.getPremio());
        existente.setHorario(referidos.getHorario());
        existente.setValidez(referidos.getValidez());
        existente.setFechaInicio(referidos.getFechaInicio());
        existente.setCategory(referidos.getCategory());
        existente.setPlantel(referidos.getPlantel());
        existente.setCelular(referidos.getCelular());
        existente.setNombre(referidos.getNombre());

        // Save the updated object
        return referidosRepository.save(existente);
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