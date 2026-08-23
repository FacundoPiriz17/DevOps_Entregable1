package com.devops.backend.game;

import jakarta.validation.constraints.NotBlank;

public record GameRequest(
        @NotBlank String name,
        @NotBlank String genre,
        @NotBlank String description) {
}
