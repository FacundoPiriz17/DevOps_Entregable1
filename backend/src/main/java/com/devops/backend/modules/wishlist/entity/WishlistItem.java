package com.devops.backend.modules.wishlist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "deseados")
public class WishlistItem {
    @EmbeddedId
    private WishlistItemId id;
    @Column(name = "fecha_agregado", nullable = false)
    private LocalDate addedAt = LocalDate.now();

    protected WishlistItem() {
    }

    public WishlistItem(String userEmail, Long gameId) {
        this.id = new WishlistItemId(gameId, userEmail);
    }

    public WishlistItemId getId() { return id; }
    public LocalDate getAddedAt() { return addedAt; }
}
