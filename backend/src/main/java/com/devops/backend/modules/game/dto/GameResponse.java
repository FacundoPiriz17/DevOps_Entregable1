package com.devops.backend.modules.game.dto;

import com.devops.backend.modules.game.entity.Game;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public record GameResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        LocalDate releaseDate,
        String studio,
        String status,
        boolean available,
        String registeredBy,
        LocalDate registeredAt,
        List<CategoryResponse> categories,
        List<GameImageResponse> images) {

    public static GameResponse from(Game game, List<GameImageResponse> images) {
        List<CategoryResponse> categories = game.getCategories().stream()
                .map(CategoryResponse::from)
                .sorted(Comparator.comparing(CategoryResponse::id))
                .toList();
        return new GameResponse(game.getId(), game.getName(), game.getDescription(), game.getPrice(),
                game.getReleaseDate(), game.getStudio(), game.getStatus().value(),
                game.getStatus().isPurchasable(), game.getRegisteredByAdminEmail(), game.getRegisteredAt(),
                categories, images);
    }
}
