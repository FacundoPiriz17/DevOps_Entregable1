package com.devops.backend.modules.session.controller;

import com.devops.backend.common.config.OpenApiConfig;
import com.devops.backend.common.security.CurrentUser;
import com.devops.backend.modules.session.dto.PlaytimeResponse;
import com.devops.backend.modules.session.dto.SessionResponse;
import com.devops.backend.modules.session.service.SessionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Sessions", description = "Inicio, finalización y consulta de sesiones de juego")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class SessionController {

    private final SessionService sessionService;
    private final CurrentUser currentUser;

    public SessionController(SessionService sessionService, CurrentUser currentUser) {
        this.sessionService = sessionService;
        this.currentUser = currentUser;
    }

    @PostMapping("/api/library/games/{gameId}/sessions:start")
    public SessionResponse start(@PathVariable Long gameId, JwtAuthenticationToken auth) {
        return sessionService.start(currentUser.idFrom(auth), gameId);
    }

    @PostMapping("/api/library/games/{gameId}/sessions:stop")
    public SessionResponse stop(@PathVariable Long gameId, JwtAuthenticationToken auth) {
        return sessionService.stop(currentUser.idFrom(auth), gameId);
    }

    @GetMapping("/api/sessions/{sessionId}")
    public SessionResponse getSession(@PathVariable Long sessionId, JwtAuthenticationToken auth) {
        return sessionService.getSession(currentUser.idFrom(auth), sessionId);
    }

    @GetMapping("/api/library/games/{gameId}/playtime")
    public PlaytimeResponse getPlaytime(@PathVariable Long gameId, JwtAuthenticationToken auth) {
        return sessionService.getTotalPlaytime(currentUser.idFrom(auth), gameId);
    }
}
