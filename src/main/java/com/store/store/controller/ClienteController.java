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
import com.store.store.model.Cliente;
import com.store.store.model.Referidos;
import com.store.store.model.Status;
import com.store.store.service.ClienteService;
import com.store.store.service.ReferidosService;
import com.store.store.utils.ResponseWrapper;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private static final Logger logger = LogManager.getLogger(ClienteController.class);

    @Autowired
    private ClienteService referidosService;

    @PostMapping("/cliente")
    public Map<String, Object> guardarUsuario(@RequestBody Cliente requestJson) {
        logger.info("Received JSON: {}", requestJson);
        try {
            // Referidos refer = objectMapper.convertValue(requestJson, Referidos.class);
            logger.info("refer object Referidos: {}", requestJson);
            referidosService.guardarCliente(requestJson);
            logger.info("Parsed JSON: {}", requestJson);
            return requestJson != null
                    ? ResponseWrapper.successNoMessages()
                    : ResponseWrapper.error(new Exception("Parsed JSON is null"), "Error in method receiveReferido",
                            requestJson.toString());
        } catch (JsonProcessingException e) {
            logger.error("Error processing JSON: {}", e.getMessage());
            return ResponseWrapper.error(e, "Error in method receiveReferido", requestJson.toString());
        } catch (Exception e) {
            logger.error("Error saving user: {}", e.getMessage());
            return ResponseWrapper.error(e, "Error in saving receiveReferido", requestJson.toString());
        }
    }

    @PostMapping("/cliente2")
    public ResponseEntity<Map<String, Object>> guardarCliente(@RequestBody Cliente referido) {
        logger.info("Received Referido: {}", referido);
        try {
            String saved = referidosService.guardarCliente(referido);
            logger.warn("Cliente saved successfully: {}", saved);
            return ResponseEntity.ok(ResponseWrapper.successNoMessages());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            ResponseWrapper.error(e, "Error saving Referido", referido));
        }
    }

    @GetMapping("/clientes")
    public List<Cliente> getAllReferidos() {
        return referidosService.obtenerTodos();
    }

    @DeleteMapping("/borraClientes")
    public ResponseEntity<Map<String, Object>> deleteAll() {
        logger.info("Deleting all Cliente");
        try {
            // referidosService.deleteReferidos();
            logger.warn("Cliente deleted successfully");
            return ResponseEntity.ok(ResponseWrapper.successNoMessages());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

}