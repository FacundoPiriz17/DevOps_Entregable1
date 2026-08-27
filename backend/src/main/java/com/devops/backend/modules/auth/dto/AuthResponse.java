package com.devops.backend.modules.auth.dto;

public record AuthResponse(String token, String name, String email, String country, String role) {
}
