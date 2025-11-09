package com.store.store.model;

import lombok.Data;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;

@Data
public class CategoryResponse {
    // Constructor to build response from Category + CategoryDetails
    public CategoryResponse(Category category, CategoryDetails details) {
        this.id = category.getId();
        this.name = category.getName();
        this.description = category.getDescription();
        this.image = category.getImage();
        this.categoryDetails = details;
    }

    @DocumentId
    private String id;

    // @Field(type = FieldType.Text)
    private String name;

    // @Field(type = FieldType.Text)
    private String description;

    // @Field(type = FieldType.Text)
    private String image;

    private CategoryDetails categoryDetails;
}