package com.devops.backend.modules.library.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "biblioteca")
public class LibraryEntry {

    @EmbeddedId
    private LibraryEntryId id;

    @Column(name = "fecha_compra", nullable = false)
    private LocalDate purchasedAt = LocalDate.now();

    @Column(name = "es_favorito", nullable = false)
    private boolean favorite;

    protected LibraryEntry() {
    }

    public LibraryEntry(String userEmail, Long gameId) {
        this.id = new LibraryEntryId(gameId, userEmail);
    }

    public LibraryEntryId getId() { return id; }
    public LocalDate getPurchasedAt() { return purchasedAt; }
    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
}
