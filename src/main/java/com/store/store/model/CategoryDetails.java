package com.store.store.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CategoryDetails {
    private String id;
    private String horario;
    private LocalDate startDate;
    private double costoInscripcion;
    private double costoColegiatura;
    private boolean admision;
}
