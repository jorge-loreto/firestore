package com.store.store.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class User {

    private String id;
    @NotNull(message = "Name is required")
    private String name;
    @NotNull(message = "Password is required")
    private String passwdString;
}
