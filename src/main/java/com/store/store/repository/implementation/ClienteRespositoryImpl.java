package com.store.store.repository.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.*;
import com.google.common.util.concurrent.MoreExecutors;
import com.store.store.model.Cliente;
import com.store.store.model.Referidos;
import com.store.store.model.Status;
import com.store.store.repository.ClienteRespository;
import com.store.store.repository.ReferidosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class ClienteRespositoryImpl implements ClienteRespository {

    private final Firestore firestore;
    private final ObjectMapper objectMapper;
    private static final String COLLECTION_NAME = "cliente";

    @Autowired
    public ClienteRespositoryImpl(Firestore firestore, ObjectMapper objectMapper) {
        this.firestore = firestore;
        this.objectMapper = objectMapper;
    }

    /** Utility to convert Firestore ApiFuture into CompletableFuture **/
    private static <T> CompletableFuture<T> toCompletableFuture(ApiFuture<T> apiFuture) {
        CompletableFuture<T> cf = new CompletableFuture<>();
        ApiFutures.addCallback(apiFuture, new ApiFutureCallback<T>() {
            @Override
            public void onFailure(Throwable t) {
                cf.completeExceptionally(t);
            }

            @Override
            public void onSuccess(T result) {
                cf.complete(result);
            }
        }, MoreExecutors.directExecutor());
        return cf;
    }

    /**
     * ───────────────────────────────
     * FIND BY ID
     * ───────────────────────────────
     **/
    @Override
    public Optional<Cliente> findById(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Referido ID cannot be null or empty.");
        }

        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        try {
            DocumentSnapshot doc = toCompletableFuture(docRef.get()).get();

            if (doc.exists()) {
                Cliente referido = objectMapper.convertValue(doc.getData(), Cliente.class);
                referido.setId(doc.getId());
                return Optional.of(referido);
            } else {
                return Optional.empty();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        } catch (ExecutionException e) {
            throw new RuntimeException("Error retrieving referido by ID: " + id, e.getCause());
        }
    }

    /**
     * ───────────────────────────────
     * FIND ALL
     * ───────────────────────────────
     **/
    @Override
    public List<Cliente> findAll() {
        try {
            ApiFuture<QuerySnapshot> queryFuture = firestore.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = toCompletableFuture(queryFuture).get().getDocuments();

            return documents.stream()
                    .map(doc -> {
                        Cliente ref = objectMapper.convertValue(doc.getData(), Cliente.class);
                        ref.setId(doc.getId());
                        return ref;
                    })
                    .collect(Collectors.toList());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Collections.emptyList();
        } catch (ExecutionException e) {
            throw new RuntimeException("Error retrieving all Cliente", e.getCause());
        }
    }

    /**
     * ───────────────────────────────
     * SAVE
     * ───────────────────────────────
     **/
    @Override
    public Cliente save(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente cannot be null.");
        }

        CollectionReference collectionRef = firestore.collection(COLLECTION_NAME);

        DocumentReference docRef;
        if (cliente.getId() == null || cliente.getId().isEmpty()) {
            docRef = collectionRef.document(); // Auto-generates an ID
            cliente.setId(docRef.getId()); // Assigns generated ID back to the object
        } else {
            docRef = collectionRef.document(cliente.getId());
        }

        Map<String, Object> docData = objectMapper.convertValue(cliente, Map.class);
        ApiFuture<WriteResult> writeFuture = docRef.set(docData);

        try {
            toCompletableFuture(writeFuture).get();
            return cliente;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to save cliente", e.getCause());
        }
    }

    /**
     * ───────────────────────────────
     * DELETE BY ID
     * ───────────────────────────────
     **/
    @Override
    public Boolean deleteById(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Referido ID cannot be null or empty.");
        }

        try {
            firestore.collection(COLLECTION_NAME).document(id).delete().get();
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to delete referido with ID: " + id, e.getCause());
        }
    }

    /**
     * ───────────────────────────────
     * DELETE ALL
     * ───────────────────────────────
     **/
    @Override
    public Boolean deleteAll() {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = toCompletableFuture(future).get().getDocuments();

            for (QueryDocumentSnapshot doc : documents) {
                doc.getReference().delete();
            }
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to delete all Cliente", e.getCause());
        }
    }

    /**
     * ───────────────────────────────
     * EDIT CLIENTE
     * ───────────────────────────────
     **/
    @Override
    public Cliente editCliente(Cliente cliente) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'editCliente'");
    }

}
