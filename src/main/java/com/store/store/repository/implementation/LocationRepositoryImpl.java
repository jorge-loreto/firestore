package com.store.store.repository.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.common.util.concurrent.MoreExecutors;
import com.store.store.model.Location;
import com.store.store.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class LocationRepositoryImpl implements LocationRepository {

    private final Firestore firestore;
    private final ObjectMapper objectMapper;
    private static final String COLLECTION_NAME = "locations";

    @Autowired
    public LocationRepositoryImpl(Firestore firestore, ObjectMapper objectMapper) {
        this.firestore = firestore;
        this.objectMapper = objectMapper;
    }

    /**
     * Utility: convert Firestore ApiFuture to CompletableFuture
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
     * Save or update a Location document.
     */
    @Override
    public Location save(Location location) {
        if (location.getId() == null || location.getId().isEmpty()) {
            throw new IllegalArgumentException("Location ID cannot be null or empty.");
        }

        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(location.getId());
        Map<String, Object> data = objectMapper.convertValue(location, Map.class);
        ApiFuture<WriteResult> future = docRef.set(data);

        try {
            toCompletableFuture(future).get();
            return location;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (ExecutionException e) {
            throw new RuntimeException("Error saving Location", e.getCause());
        }
    }

    /**
     * Delete all Location documents.
     */
    @Override
    public Boolean deleteAll() {
        try {
            ApiFuture<QuerySnapshot> queryFuture = firestore.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = toCompletableFuture(queryFuture).get().getDocuments();

            for (QueryDocumentSnapshot doc : documents) {
                doc.getReference().delete();
            }
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to delete all locations", e);
        }
    }

    /**
     * Delete a single Location by ID.
     */
    @Override
    public Boolean deleteById(String id) {
        try {
            firestore.collection(COLLECTION_NAME).document(id).delete();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Count all Location documents.
     */
    @Override
    public long count() {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
            return toCompletableFuture(future).get().size();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return 0;
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to count locations", e);
        }
    }

    /**
     * Find a single Location by ID.
     */
    @Override
    public Optional<Location> findById(String id) {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        try {
            DocumentSnapshot doc = toCompletableFuture(docRef.get()).get();
            return doc.exists()
                    ? Optional.ofNullable(doc.toObject(Location.class))
                    : Optional.empty();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        } catch (ExecutionException e) {
            throw new RuntimeException("Error finding Location by ID", e.getCause());
        }
    }

    /**
     * Find all Locations by their IDs.
     */
    @Override
    public Iterable<Location> findAllById(Iterable<String> ids) {
        List<Location> results = new ArrayList<>();
        for (String id : ids) {
            findById(id).ifPresent(results::add);
        }
        return results;
    }

    /**
     * Find all Location documents.
     */
    @Override
    public List<Location> findAll() {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = toCompletableFuture(future).get().getDocuments();
            return documents.stream()
                    .map(doc -> doc.toObject(Location.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Collections.emptyList();
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to fetch all locations", e);
        }
    }

    /**
     * Save a batch of Location documents.
     */
    @Override
    public Iterable<Location> saveAll(Iterable<Location> locations) {
        List<Location> saved = new ArrayList<>();
        for (Location loc : locations) {
            Location result = save(loc);
            if (result != null) {
                saved.add(result);
            }
        }
        return saved;
    }
}
