package com.store.store.model;

import lombok.Data;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;

@Document(collectionName = "categoryDetails")
@Data
public class CategoryDetails {

    private @DocumentId @com.fasterxml.jackson.annotation.JsonIgnore String id;
    private String horario;
    private String startDate;
    private double costoInscripcion;
    private double costoColegiatura;
    private boolean admision;
}
