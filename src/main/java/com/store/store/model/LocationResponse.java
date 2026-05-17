package com.store.store.model;

import java.util.List;
import lombok.Data;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;

@Document(collectionName = "locations")
@Data
public class LocationResponse {

    @DocumentId
    private String id;
    private String name;
    private String description;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String phone;
    private List<String> categoriesList;
    private List<CategoryResponse> categories;
    private String tarjetaBancomer;
    private String oxxo;
}
