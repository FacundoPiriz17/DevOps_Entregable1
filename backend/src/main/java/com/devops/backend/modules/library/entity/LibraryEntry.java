package com.devops.backend.modules.library.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "library_entries")
public class LibraryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "game_id", nullable = false)
    private Long gameId;

    @Column(name = "added_at", nullable = false)
    private Instant addedAt = Instant.now();

    protected LibraryEntry() {
    }

    public LibraryEntry(Long userId, Long gameId) {
        this.userId = userId;
        this.gameId = gameId;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getGameId() {
        return gameId;
    }

    public Instant getAddedAt() {
        return addedAt;
    }
}
