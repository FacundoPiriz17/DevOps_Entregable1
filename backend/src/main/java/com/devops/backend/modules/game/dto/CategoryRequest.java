package com.devops.backend.modules.game.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CategoryRequest(
        @NotBlank String name,
        @NotBlank @Pattern(regexp = "genero|etiqueta") String type) {
}
