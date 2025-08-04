package com.store.store.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import com.google.cloud.spring.data.firestore.Document;

//import org.springframework.data.annotation.Id;
@Document(collectionName = "categories")
@Data
public class Category {
    // @Id
    private String id;

    // @Field(type = FieldType.Text)
    private String name;

    // @Field(type = FieldType.Text)
    private String description;

    // @Field(type = FieldType.Text)
    private String image;

    private CategoryDetails categoryDetails;
}
