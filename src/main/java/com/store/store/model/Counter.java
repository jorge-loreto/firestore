package com.store.store.model;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.Data;

@Data
public class Counter {
    @DocumentId
    private String id; // e.g., "global_counter"
    private Long value;
}
