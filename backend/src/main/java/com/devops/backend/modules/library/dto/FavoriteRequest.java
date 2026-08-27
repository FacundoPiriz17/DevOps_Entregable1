package com.devops.backend.modules.library.dto;

import jakarta.validation.constraints.NotNull;

public record FavoriteRequest(@NotNull Boolean favorite) {
}
