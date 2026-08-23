package com.devops.backend.modules.user.dto;

import com.devops.backend.modules.user.entity.User;
import java.time.Instant;

public record UserBasicResponse(Long id, String name, String email, String role, boolean active, Instant createdAt) {

    public static UserBasicResponse from(User user) {
        return new UserBasicResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().name(),
                user.isActive(), user.getCreatedAt());
    }
}
