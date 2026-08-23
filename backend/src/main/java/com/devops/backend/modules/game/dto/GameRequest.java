package com.devops.backend.modules.game.dto;

import jakarta.validation.constraints.NotBlank;

public record GameRequest(
        @NotBlank String name,
        @NotBlank String genre,
        @NotBlank String description) {
}
