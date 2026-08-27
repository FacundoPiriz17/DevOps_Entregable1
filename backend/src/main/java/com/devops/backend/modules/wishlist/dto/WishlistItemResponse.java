package com.devops.backend.modules.wishlist.dto;

import com.devops.backend.modules.game.entity.Game;
import com.devops.backend.modules.wishlist.entity.WishlistItem;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WishlistItemResponse(
        Long gameId, String name, BigDecimal price, String status, LocalDate addedAt) {
    public static WishlistItemResponse from(WishlistItem item, Game game) {
        return new WishlistItemResponse(game.getId(), game.getName(), game.getPrice(),
                game.getStatus().value(), item.getAddedAt());
    }
}
