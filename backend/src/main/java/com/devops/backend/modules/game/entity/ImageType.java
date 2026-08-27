package com.devops.backend.modules.game.entity;

import jakarta.persistence.EnumeratedValue;

import java.util.Locale;

public enum ImageType {
    PORTADA("portada"),
    BANNER("banner"),
    GALERIA("galeria");

    @EnumeratedValue
    private final String databaseValue;

    ImageType(String databaseValue) { this.databaseValue = databaseValue; }
    public String value() { return databaseValue; }
    public static ImageType fromValue(String value) {
        return valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
