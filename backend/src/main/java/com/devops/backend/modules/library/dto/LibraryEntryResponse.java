package com.devops.backend.modules.library.dto;

import com.devops.backend.modules.game.entity.Game;
import com.devops.backend.modules.library.entity.LibraryEntry;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LibraryEntryResponse(
        Long gameId,
        String name,
        String description,
        BigDecimal price,
        String studio,
        LocalDate purchasedAt,
        boolean favorite) {

    public static LibraryEntryResponse from(LibraryEntry entry, Game game) {
        return new LibraryEntryResponse(game.getId(), game.getName(), game.getDescription(), game.getPrice(),
                game.getStudio(), entry.getPurchasedAt(), entry.isFavorite());
    }
}
