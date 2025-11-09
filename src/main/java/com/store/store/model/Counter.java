package com.store.store.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;

import lombok.Data;

@Document(collectionName = "counter")
@Data
public class Counter {
    @DocumentId
    private String id; // e.g., "global_counter"
    private Long value;
}
