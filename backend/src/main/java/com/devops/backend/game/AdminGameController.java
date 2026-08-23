package com.devops.backend.game;

import com.devops.backend.common.CurrentUser;
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
public class AdminGameController {

    private final GameService gameService;
    private final CurrentUser currentUser;

    public AdminGameController(GameService gameService, CurrentUser currentUser) {
        this.gameService = gameService;
        this.currentUser = currentUser;
    }

    @PostMapping
    public ResponseEntity<GameResponse> create(@Valid @RequestBody GameRequest request, JwtAuthenticationToken auth) {
        GameResponse created = gameService.create(request, currentUser.idFrom(auth));
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
