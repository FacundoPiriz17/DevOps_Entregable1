package com.devops.backend.modules.auth.dto;

public record AuthResponse(String token, Long userId, String name, String email, String role) {
}
