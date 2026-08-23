package com.devops.backend.library;

import com.devops.backend.game.Game;

import java.time.Instant;

public record LibraryEntryResponse(
        Long gameId,
        String name,
        String genre,
        String description,
        Instant addedAt) {

    public static LibraryEntryResponse from(LibraryEntry entry, Game game) {
        return new LibraryEntryResponse(
                game.getId(),
                game.getName(),
                game.getGenre(),
                game.getDescription(),
                entry.getAddedAt());
    }
}
