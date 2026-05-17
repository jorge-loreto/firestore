package com.store.store.repository.implementation;

import com.google.cloud.firestore.Firestore;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.core.ApiFutureCallback;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.store.model.Category;
import com.store.store.model.CategoryDetails;
import com.store.store.repository.CategoryDetailsRepository;
import com.store.store.service.CategoryService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import com.google.common.util.concurrent.MoreExecutors;

@Repository
public class CategoryDetailsRepositoryImpl implements CategoryDetailsRepository {

    private final Firestore firestore;
    private final ObjectMapper objectMapper;
    private final String COLLECTION_NAME = "categoryDetails";
    private static final Logger logger = LogManager.getLogger(CategoryDetailsRepositoryImpl.class);

    @Autowired
    public CategoryDetailsRepositoryImpl(Firestore firestore, ObjectMapper objectMapper) {
        this.firestore = firestore;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<CategoryDetails> save(CategoryDetails categoryDetails) {
        if (categoryDetails.getId() == null || categoryDetails.getId().isEmpty()) {
            return Mono.error(new IllegalArgumentException("CategoryDetails ID cannot be null or empty for saving."));
        }

        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(categoryDetails.getId());
        Map<String, Object> docData = objectMapper.convertValue(categoryDetails, Map.class);
        ApiFuture<WriteResult> writeResultApiFuture = docRef.set(docData);

        CompletableFuture<WriteResult> writeResultCf = toCompletableFuture(writeResultApiFuture);

        return Mono.fromFuture(writeResultCf)
                .thenReturn(categoryDetails);
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
    public Optional<CategoryDetails> findById(String id) {
        logger.info("Finding CategoryDetails by ID: {}", id);
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        try {
            DocumentSnapshot doc = toCompletableFuture(docRef.get()).get();
            if (doc.exists()) {
                return Optional.ofNullable(doc.toObject(CategoryDetails.class));
            } else {
                return Optional.empty();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to get document from Firestore", e.getCause());
        }
    }

    @Override
    public Iterable<Category> findAll() {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = toCompletableFuture(future).get().getDocuments();
            return documents.stream()
                    .map(doc -> doc.toObject(Category.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Collections.emptyList();
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to fetch documents", e);
        }
    }

    @Override
    public Boolean save(Category category) {
        if (category.getId() == null || category.getId().isEmpty()) {
            throw new IllegalArgumentException("Category ID cannot be null or empty.");
        }

        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(category.getId());
        Map<String, Object> data = objectMapper.convertValue(category, Map.class);
        ApiFuture<WriteResult> result = docRef.set(data);
        try {
            toCompletableFuture(result).get();
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error saving category", e);
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
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete all documents", e);
        }
    }

    @Override
    public long count() {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
            return toCompletableFuture(future).get().size();
        } catch (Exception e) {
            throw new RuntimeException("Failed to count documents", e);
        }
    }

    @Override
    public boolean existsById(String id) {
        return findById(id).isPresent();
    }

    @Override
    public Iterable<Category> findAllById(Iterable<String> ids) {
        List<Category> results = new ArrayList<>();
        for (String id : ids) {
            findById(id).ifPresent(cd -> results.add(objectMapper.convertValue(cd, Category.class)));
        }
        return results;
    }

    @Override
    public Iterable<Category> saveAll(Iterable<Category> categories) {
        List<Category> saved = new ArrayList<>();
        for (Category category : categories) {
            if (save(category)) {
                saved.add(category);
            }
        }
        return saved;
    }

    @Override
    public void delete(Category category) {
        if (category.getId() != null) {
            deleteById(category.getId());
        }
    }

    @Override
    public void deleteAll(Iterable<? extends Category> categories) {
        for (Category category : categories) {
            delete(category);
        }
    }

    @Override
    public void deleteAllCategories() {
        deleteAll();
    }

    @Override
    public boolean updateCategory(Category category) {
        if (category.getId() == null) {
            throw new IllegalArgumentException("Category ID cannot be null for update.");
        }

        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(category.getId());
        Map<String, Object> data = objectMapper.convertValue(category, Map.class);
        ApiFuture<WriteResult> result = docRef.update(data);
        try {
            toCompletableFuture(result).get();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateCategoryDetails(CategoryDetails categoryDetails) {
        if (categoryDetails.getId() == null) {
            throw new IllegalArgumentException("CategoryDetails ID cannot be null for update.");
        }

        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(categoryDetails.getId());
        Map<String, Object> data = objectMapper.convertValue(categoryDetails, Map.class);
        ApiFuture<WriteResult> result = docRef.update(data);
        try {
            toCompletableFuture(result).get();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}