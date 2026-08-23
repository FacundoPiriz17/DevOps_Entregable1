package com.devops.backend.modules.user.dto;

public record GameUsageItem(Long gameId, String gameName, long totalMinutes) {
}
