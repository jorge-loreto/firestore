package com.store.store.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.store.model.Category;
import com.store.store.model.Referidos;
import com.store.store.service.ReferidosService;
import com.store.store.utils.ResponseWrapper;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/referidos")
public class ReferidosController {

    private static final Logger logger = LogManager.getLogger(ReferidosController.class);

    @Autowired
    private ReferidosService referidosService;

    @PostMapping("/referido")
    public Map<String, Object> guardarUsuario(@RequestBody String requestJson) {
        logger.info("Received JSON: {}", requestJson);

        ObjectMapper objectMapper = new ObjectMapper(); // Ensure it's an ObjectMapper
        TypeReference<Map<String, Object>> typeRef = new TypeReference<>() {
        };
        Map<String, Object> parsedJson = null;
        try {
            parsedJson = objectMapper.readValue(requestJson, typeRef);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Map<String, Object> payload = (Map<String, Object>) parsedJson.get("payload");
        // Assuming payload is a Map<String, Object>
        Referidos refer = objectMapper.convertValue(payload, Referidos.class);
        // Referidos refer = objectMapper.convertValue(requestJson, Referidos.class);
        logger.info("refer object Referidos: {}", refer);
        // Map<String, Object> payload = (Map<String, Object>)
        // parsedJson.get("payload");
        try {

            // Referidos refer = objectMapper.convertValue(requestJson, Referidos.class);
            logger.info("refer object Referidos: {}", refer);
            referidosService.guardarReferido(refer);
            logger.info("Parsed JSON: {}", parsedJson);
            return parsedJson;
        } catch (JsonProcessingException e) {
            logger.error("Error processing JSON: {}", e.getMessage());
            return ResponseWrapper.error(e, "Error in method receiveReferido", payload);
        } catch (Exception e) {
            logger.error("Error saving user: {}", e.getMessage());
            return ResponseWrapper.error(e, "Error in saving receiveReferido", payload);
        }
    }

    @GetMapping("/referidos")
    public Flux<Referidos> getAllReferidos() {
        return referidosService.obtenerTodosReferidos();
    }

    @DeleteMapping("/borrareferidos")
    public ResponseEntity<Map<String, Object>> deleteAll() {
        logger.info("Deleting all referidos");
        try {
            // referidosService.deleteReferidos();
            logger.warn("Referidos deleted successfully");
            return ResponseEntity.ok(ResponseWrapper.successNoMessages());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }
}