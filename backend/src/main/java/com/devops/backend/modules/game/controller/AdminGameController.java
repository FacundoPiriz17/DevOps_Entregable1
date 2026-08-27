package com.devops.backend.modules.game.controller;

import com.devops.backend.common.config.OpenApiConfig;
import com.devops.backend.common.security.CurrentUser;
import com.devops.backend.modules.game.dto.GameRequest;
import com.devops.backend.modules.game.dto.GameResponse;
import com.devops.backend.modules.game.service.GameService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/games")
@Tag(name = "Game administration", description = "Alta, modificación y baja lógica de videojuegos")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class AdminGameController {

    private final GameService gameService;
    private final CurrentUser currentUser;

    public AdminGameController(GameService gameService, CurrentUser currentUser) {
        this.gameService = gameService;
        this.currentUser = currentUser;
    }

    @PostMapping
    public ResponseEntity<GameResponse> create(@Valid @RequestBody GameRequest request, JwtAuthenticationToken auth) {
        GameResponse created = gameService.create(request, currentUser.emailFrom(auth));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public GameResponse update(@PathVariable Long id, @Valid @RequestBody GameRequest request) {
        return gameService.update(id, request);
    }

    @PatchMapping("/{id}/deactivate")
    public GameResponse deactivate(@PathVariable Long id) {
        return gameService.deactivate(id);
    }
}
