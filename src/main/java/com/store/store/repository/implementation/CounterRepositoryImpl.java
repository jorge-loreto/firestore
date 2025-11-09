package com.store.store.repository.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.common.util.concurrent.MoreExecutors;
import com.store.store.model.Counter;
import com.store.store.repository.CounterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Repository
public class CounterRepositoryImpl implements CounterRepository {

    private final Firestore firestore;
    private final ObjectMapper objectMapper;
    private static final String COLLECTION_NAME = "counter";

    @Autowired
    public CounterRepositoryImpl(Firestore firestore, ObjectMapper objectMapper) {
        this.firestore = firestore;
        this.objectMapper = objectMapper;
    }

    /**
     * Utility method to convert ApiFuture to CompletableFuture
     */
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
     * Find a Counter document by ID from Firestore.
     */
    @Override
    public Counter findById(String id) {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        try {
            DocumentSnapshot document = toCompletableFuture(docRef.get()).get();
            if (document.exists()) {
                return document.toObject(Counter.class);
            } else {
                return null; // no document found
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (ExecutionException e) {
            throw new RuntimeException("Error fetching Counter from Firestore", e.getCause());
        }
    }

    /**
     * Save or update a Counter document in Firestore.
     */
    @Override
    public Counter save(Counter counter) {
        if (counter.getId() == null || counter.getId().isEmpty()) {
            throw new IllegalArgumentException("Counter ID cannot be null or empty.");
        }

        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(counter.getId());
        Map<String, Object> data = objectMapper.convertValue(counter, Map.class);

        ApiFuture<WriteResult> writeFuture = docRef.set(data);
        try {
            toCompletableFuture(writeFuture).get();
            return counter; // return the saved object
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to save Counter in Firestore", e.getCause());
        }
    }
}
