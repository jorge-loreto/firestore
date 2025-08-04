package com.store.store.model;

import lombok.Data;
import java.util.List;

import com.google.cloud.spring.data.firestore.Document;

@Document(collectionName = "locations")
@Data
public class Location {
    private String id;
    private String name;
    private String description;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String phone;
    private List<String> categoriesList;
    private List<Category> categories;
    private String tarjetaBancomer;
    private String oxxo;
}
