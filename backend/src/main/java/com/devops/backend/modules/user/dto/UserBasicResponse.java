package com.devops.backend.modules.user.dto;

import com.devops.backend.modules.user.entity.Role;
import com.devops.backend.modules.user.entity.User;

import java.time.LocalDate;

public record UserBasicResponse(
        String name,
        String email,
        String country,
        String role,
        boolean active,
        LocalDate registeredAt) {

    public static UserBasicResponse from(User user, Role role) {
        return new UserBasicResponse(user.getName(), user.getEmail(), user.getCountry(), role.name(),
                user.isActive(), user.getRegisteredAt());
    }
}
