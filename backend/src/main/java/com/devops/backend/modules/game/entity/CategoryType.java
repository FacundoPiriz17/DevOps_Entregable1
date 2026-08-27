package com.devops.backend.modules.game.entity;

import jakarta.persistence.EnumeratedValue;

import java.util.Locale;

public enum CategoryType {
    GENERO("genero"),
    ETIQUETA("etiqueta");

    @EnumeratedValue
    private final String databaseValue;

    CategoryType(String databaseValue) { this.databaseValue = databaseValue; }
    public String value() { return databaseValue; }
    public static CategoryType fromValue(String value) {
        return valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
