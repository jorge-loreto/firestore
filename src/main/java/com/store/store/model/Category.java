package com.store.store.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;

@Document(collectionName = "categories")
@Data
public class Category {
    @DocumentId
    private String id;
    private String name;
    private String description;
    private String image;
}
