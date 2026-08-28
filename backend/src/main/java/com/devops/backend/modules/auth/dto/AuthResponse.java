package com.devops.backend.modules.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

public record AuthResponse(
        @JsonIgnore @Schema(hidden = true) String token,
        String name,
        String email,
        String country,
        String role) {
}
