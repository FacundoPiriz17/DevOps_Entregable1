package com.devops.backend.modules.game.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record GameImageRequest(
        @NotBlank String url,
        String alternativeText,
        @NotBlank @Pattern(regexp = "portada|banner|galeria") String type) {
}
