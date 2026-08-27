package com.devops.backend.modules.cart.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "carrito")
public class CartItem {
    @EmbeddedId
    private CartItemId id;

    protected CartItem() {
    }

    public CartItem(String userEmail, Long gameId) {
        this.id = new CartItemId(gameId, userEmail);
    }

    public CartItemId getId() { return id; }
}
