package com.devops.backend.modules.cart.dto;

import com.devops.backend.modules.game.entity.Game;

import java.math.BigDecimal;

public record CartItemResponse(Long gameId, String name, BigDecimal price, String status) {
    public static CartItemResponse from(Game game) {
        return new CartItemResponse(game.getId(), game.getName(), game.getPrice(), game.getStatus().value());
    }
}
