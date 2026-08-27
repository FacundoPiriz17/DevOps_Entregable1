package com.devops.backend.modules.game.entity;

import jakarta.persistence.EnumeratedValue;

import java.util.Locale;

public enum GameStatus {
    PUBLICADO("publicado"),
    PAUSADO("pausado"),
    PREVENTA("preventa"),
    RETIRADO("retirado");

    @EnumeratedValue
    private final String databaseValue;

    GameStatus(String databaseValue) {
        this.databaseValue = databaseValue;
    }

    public String value() { return databaseValue; }

    public boolean isPurchasable() {
        return this == PUBLICADO || this == PREVENTA;
    }

    public static GameStatus fromValue(String value) {
        return valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
