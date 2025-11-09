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
import com.store.store.model.Category;
import com.store.store.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    private final Firestore firestore;
    private final ObjectMapper objectMapper;
    private static final String COLLECTION_NAME = "categories";

    @Autowired
    public CategoryRepositoryImpl(Firestore firestore, ObjectMapper objectMapper) {
        this.firestore = firestore;
        this.objectMapper = objectMapper;
    }

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
    public boolean save(Category category) {
        if (category.getId() == null || category.getId().isEmpty()) {
            throw new IllegalArgumentException("Category ID cannot be null or empty.");
        }

        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(category.getId());
        Map<String, Object> docData = objectMapper.convertValue(category, Map.class);

        ApiFuture<WriteResult> writeFuture = docRef.set(docData);
        try {
            toCompletableFuture(writeFuture).get();
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to save category", e.getCause());
        }
    }

    @Override
    public void deleteById(String id) {
        firestore.collection(COLLECTION_NAME).document(id).delete();
    }

    @Override
    public void deleteAll() {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = toCompletableFuture(future).get().getDocuments();
            for (QueryDocumentSnapshot doc : documents) {
                doc.getReference().delete();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to delete all categories", e);
        }
    }

    @Override
    public long count() {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
            QuerySnapshot snapshot = toCompletableFuture(future).get();
            return snapshot.size();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return 0;
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to count categories", e);
        }
    }

    @Override
    public boolean findById2(String id) {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        try {
            DocumentSnapshot doc = toCompletableFuture(docRef.get()).get();
            return doc.exists();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (ExecutionException e) {
            throw new RuntimeException("Error retrieving category", e.getCause());
        }
    }

    @Override
    public Optional<Category> findById(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Category ID cannot be null or empty.");
        }

        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        try {
            DocumentSnapshot doc = toCompletableFuture(docRef.get()).get();

            if (doc.exists()) {
                Category category = objectMapper.convertValue(doc.getData(), Category.class);
                category.setId(doc.getId()); // ensure ID is included
                return Optional.of(category);
            } else {
                return Optional.empty();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        } catch (ExecutionException e) {
            throw new RuntimeException("Error retrieving category by ID: " + id, e.getCause());
        }
    }

}
