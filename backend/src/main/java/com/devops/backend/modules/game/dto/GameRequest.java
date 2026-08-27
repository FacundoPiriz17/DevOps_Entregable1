package com.devops.backend.modules.game.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record GameRequest(
        @NotBlank String name,
        @NotBlank String description,
        @NotNull @DecimalMin("0.00") @Digits(integer = 8, fraction = 2) BigDecimal price,
        @NotNull LocalDate releaseDate,
        @NotBlank String studio,
        @NotBlank @Pattern(regexp = "publicado|pausado|preventa|retirado") String status,
        Set<Long> categoryIds) {
}
