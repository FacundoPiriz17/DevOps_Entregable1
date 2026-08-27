package com.devops.backend.modules.cart.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CartItemId implements Serializable {
    @Column(name = "identificador_juego")
    private Long gameId;
    @Column(name = "email_general")
    private String userEmail;

    protected CartItemId() {
    }

    public CartItemId(Long gameId, String userEmail) {
        this.gameId = gameId;
        this.userEmail = userEmail;
    }

    public Long getGameId() { return gameId; }
    public String getUserEmail() { return userEmail; }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof CartItemId that)) return false;
        return Objects.equals(gameId, that.gameId) && Objects.equals(userEmail, that.userEmail);
    }

    @Override
    public int hashCode() { return Objects.hash(gameId, userEmail); }
}
