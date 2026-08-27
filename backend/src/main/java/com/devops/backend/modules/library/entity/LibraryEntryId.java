package com.devops.backend.modules.library.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LibraryEntryId implements Serializable {
    @Column(name = "identificador_juego")
    private Long gameId;
    @Column(name = "email_general")
    private String userEmail;

    protected LibraryEntryId() {
    }

    public LibraryEntryId(Long gameId, String userEmail) {
        this.gameId = gameId;
        this.userEmail = userEmail;
    }

    public Long getGameId() { return gameId; }
    public String getUserEmail() { return userEmail; }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof LibraryEntryId that)) return false;
        return Objects.equals(gameId, that.gameId) && Objects.equals(userEmail, that.userEmail);
    }

    @Override
    public int hashCode() { return Objects.hash(gameId, userEmail); }
}
