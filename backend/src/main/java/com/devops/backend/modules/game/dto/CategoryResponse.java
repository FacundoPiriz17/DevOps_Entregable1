package com.devops.backend.modules.game.dto;

import com.devops.backend.modules.game.entity.Category;

public record CategoryResponse(Long id, String name, String type) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getType().value());
    }
}
