package com.devops.backend.modules.game.controller;

import com.devops.backend.common.config.OpenApiConfig;
import com.devops.backend.modules.game.dto.GameImageRequest;
import com.devops.backend.modules.game.dto.GameImageResponse;
import com.devops.backend.modules.game.service.GameImageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/games/{gameId}/images")
@Tag(name = "Game image administration", description = "Gestión de imágenes asociadas a videojuegos")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class AdminGameImageController {
    private final GameImageService gameImageService;
    public AdminGameImageController(GameImageService gameImageService) { this.gameImageService = gameImageService; }

    @PostMapping
    public ResponseEntity<GameImageResponse> add(@PathVariable Long gameId,
                                                  @Valid @RequestBody GameImageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gameImageService.add(gameId, request));
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> remove(@PathVariable Long gameId, @PathVariable Long imageId) {
        gameImageService.remove(gameId, imageId);
        return ResponseEntity.noContent().build();
    }
}
