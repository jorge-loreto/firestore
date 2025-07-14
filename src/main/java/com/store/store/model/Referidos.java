package com.store.store.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//import com.google.cloud.spring.data.firestore.Document;
import org.springframework.data.annotation.Id;

@JsonInclude(JsonInclude.Include.NON_NULL) // Ensures no fields are skipped
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // Ignores unknown fields
@Document(collectionName = "referidos")
public class Referidos {

    @DocumentId
    private String id; // Unique identifier for the question

    private String nombre;

    private String celular;

    @JsonProperty("plantel") // JSON field name
    private String plantel;

    @JsonProperty("category") // JSON field name
    private String category;

    @JsonProperty("fechaInicio") // JSON field name
    private String fechaInicio;

    @JsonProperty("premio") // JSON field name
    private String premio;

    @JsonProperty("horario") // JSON field name
    private String horario;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    private String validez;

    @JsonProperty("fecha") // JSON field name
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String creationDate;

    @JsonProperty("inscripcion") // JSON field name
    private Float inscripcion;

}