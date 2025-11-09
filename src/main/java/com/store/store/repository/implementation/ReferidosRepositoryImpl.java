package com.store.store.repository.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.*;
import com.google.common.util.concurrent.MoreExecutors;
import com.store.store.model.Referidos;
import com.store.store.model.Status;
import com.store.store.repository.ReferidosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class ReferidosRepositoryImpl implements ReferidosRepository {

    private final Firestore firestore;
    private final ObjectMapper objectMapper;
    private static final String COLLECTION_NAME = "referidos";

    @Autowired
    public ReferidosRepositoryImpl(Firestore firestore, ObjectMapper objectMapper) {
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
    public Optional<Referidos> findById(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Referido ID cannot be null or empty.");
        }

        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        try {
            DocumentSnapshot doc = toCompletableFuture(docRef.get()).get();

            if (doc.exists()) {
                Referidos referido = objectMapper.convertValue(doc.getData(), Referidos.class);
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
    public List<Referidos> findAll() {
        try {
            ApiFuture<QuerySnapshot> queryFuture = firestore.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = toCompletableFuture(queryFuture).get().getDocuments();

            return documents.stream()
                    .map(doc -> {
                        Referidos ref = objectMapper.convertValue(doc.getData(), Referidos.class);
                        ref.setId(doc.getId());
                        return ref;
                    })
                    .collect(Collectors.toList());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Collections.emptyList();
        } catch (ExecutionException e) {
            throw new RuntimeException("Error retrieving all referidos", e.getCause());
        }
    }

    /**
     * ───────────────────────────────
     * SAVE
     * ───────────────────────────────
     **/
    @Override
    public Referidos save(Referidos referidos) {
        if (referidos.getId() == null || referidos.getId().isEmpty()) {
            throw new IllegalArgumentException("Referido ID cannot be null or empty.");
        }

        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(referidos.getId());
        Map<String, Object> docData = objectMapper.convertValue(referidos, Map.class);

        ApiFuture<WriteResult> writeFuture = docRef.set(docData);
        try {
            toCompletableFuture(writeFuture).get();
            return referidos;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to save referido", e.getCause());
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
            throw new RuntimeException("Failed to delete all referidos", e.getCause());
        }
    }

    @Override
    public List<Referidos> findByStatus(Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null.");
        }

        try {
            // Query Firestore where the "status" field matches the given status name
            ApiFuture<QuerySnapshot> queryFuture = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("status", status.name())
                    .get();

            // Retrieve all matching documents
            List<QueryDocumentSnapshot> documents = toCompletableFuture(queryFuture).get().getDocuments();

            // Convert documents to Referidos objects
            return documents.stream()
                    .map(doc -> {
                        Referidos referido = objectMapper.convertValue(doc.getData(), Referidos.class);
                        referido.setId(doc.getId()); // Ensure Firestore document ID is assigned
                        return referido;
                    })
                    .collect(Collectors.toList());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Collections.emptyList();
        } catch (ExecutionException e) {
            throw new RuntimeException("Error retrieving referidos by status: " + status, e.getCause());
        }
    }

}
