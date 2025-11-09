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

import jakarta.validation.constraints.NotNull;
//import javax.validation.constraints.NotNull;
//import com.google.cloud.spring.data.firestore.Document;
import org.springframework.data.annotation.Id;

@JsonInclude(JsonInclude.Include.NON_NULL) // Ensures no fields are skipped
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // Ignores unknown fields
@Document(collectionName = "clientes")
public class Cliente {

    @DocumentId
    private String id; // Unique identifier for the question

    @NotNull(message = "Name is required")
    private String nombre;

    private String celular;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String creationDate;

    private String notas;

}