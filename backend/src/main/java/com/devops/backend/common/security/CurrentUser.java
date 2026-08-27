package com.devops.backend.common.security;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    public String emailFrom(JwtAuthenticationToken authentication) {
        return authentication.getToken().getSubject();
    }
}
