package com.store.store.repository.implementation;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.cloud.firestore.WriteResult;
import com.store.store.model.Business;
import com.store.store.model.Cliente;
import com.store.store.repository.BusinessRepository;

@Repository
public class BusinessRepositoryImpl implements BusinessRepository {

    private final Firestore firestore;
    private final ObjectMapper objectMapper;
    private static final String COLLECTION_NAME = "businesses";

    @Autowired
    public BusinessRepositoryImpl(Firestore firestore, ObjectMapper objectMapper) {
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

    @Override
    public Optional<Business> findById(String id) {
        if (id == null || id.isEmpty()) {
            return Optional.empty();
        }
        try {
            CompletableFuture<DocumentSnapshot> future = toCompletableFuture(
                    firestore.collection(COLLECTION_NAME).document(id).get());
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                Business business = document.toObject(Business.class);
                return Optional.ofNullable(business);
            } else {
                return Optional.empty();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * ───────────────────────────────
     * FIND ALL
     * ───────────────────────────────
     **/
    @Override
    public List<Business> findAll() {
        try {
            ApiFuture<QuerySnapshot> queryFuture = firestore.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = toCompletableFuture(queryFuture).get().getDocuments();

            return documents.stream()
                    .map(doc -> {
                        Business ref = objectMapper.convertValue(doc.getData(), Business.class);
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

    @Override
    public Business save(Business business) {
        try {
            CollectionReference collectionRef = firestore.collection(COLLECTION_NAME);
            DocumentReference docRef;
            if (business.getId() != null && !business.getId().isEmpty()) {
                docRef = collectionRef.document(business.getId());
            } else {
                docRef = collectionRef.document();
                business.setId(docRef.getId());
            }

            ApiFuture<com.google.cloud.firestore.WriteResult> writeResult = docRef.set(business);
            writeResult.get(); // Wait for the write to complete
            return business;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error saving Business: Operation was interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Error saving Business", e.getCause());
        }
    }

    @Override
    public Boolean deleteAll() {
        try {
            ApiFuture<QuerySnapshot> queryFuture = firestore.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = toCompletableFuture(queryFuture).get().getDocuments();

            List<ApiFuture<WriteResult>> deleteFutures = documents.stream()
                    .map(doc -> firestore.collection(COLLECTION_NAME).document(doc.getId()).delete())
                    .collect(Collectors.toList());

            // Wait for all deletions to complete
            for (ApiFuture<WriteResult> future : deleteFutures) {
                future.get();
            }
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (ExecutionException e) {
            throw new RuntimeException("Error deleting all Business records", e.getCause());
        }
    }

    @Override
    public Boolean deleteById(String id) {
        try {
            ApiFuture<WriteResult> deleteFuture = firestore.collection(COLLECTION_NAME).document(id).delete();
            deleteFuture.get(); // Wait for deletion to complete
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (ExecutionException e) {
            throw new RuntimeException("Error deleting Business with id: " + id, e.getCause());
        }
    }

    @Override
    public Business edit(Business cliente) {
        if (cliente.getId() == null || cliente.getId().isEmpty()) {
            throw new IllegalArgumentException("Business ID cannot be null or empty for edit operation.");
        }

        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(cliente.getId());
        Map<String, Object> docData = objectMapper.convertValue(cliente, Map.class);
        ApiFuture<WriteResult> writeFuture = docRef.set(docData);

        try {
            toCompletableFuture(writeFuture).get();
            return cliente;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to save Business", e.getCause());
        }
    }

}
