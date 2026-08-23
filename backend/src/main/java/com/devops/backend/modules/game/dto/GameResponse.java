package com.devops.backend.modules.game.dto;

import com.devops.backend.modules.game.entity.Game;
import com.devops.backend.modules.game.entity.GameStatus;

public record GameResponse(
        Long id,
        String name,
        String genre,
        String description,
        String status,
        boolean available) {

    public static GameResponse from(Game game) {
        return new GameResponse(
                game.getId(),
                game.getName(),
                game.getGenre(),
                game.getDescription(),
                game.getStatus().name(),
                game.getStatus() == GameStatus.ACTIVE);
    }
}
