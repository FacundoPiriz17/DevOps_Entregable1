package com.devops.backend.modules.library.dto;

import com.devops.backend.modules.game.entity.Game;
import com.devops.backend.modules.library.entity.LibraryEntry;

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
