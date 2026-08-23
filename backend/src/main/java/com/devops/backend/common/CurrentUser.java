package com.devops.backend.common;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    public Long idFrom(JwtAuthenticationToken authentication) {
        Jwt jwt = authentication.getToken();
        return Long.valueOf(jwt.getSubject());
    }
}
