package com.devops.backend.game;

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
