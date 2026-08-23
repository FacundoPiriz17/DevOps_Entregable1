package com.devops.backend.modules.game.controller;

import com.devops.backend.common.config.OpenApiConfig;
import com.devops.backend.modules.game.dto.GameResponse;
import com.devops.backend.modules.game.service.GameService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@Tag(name = "Games", description = "Consulta del catálogo de videojuegos")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public List<GameResponse> listGames() {
        return gameService.listAll();
    }

    @GetMapping("/{id}")
    public GameResponse getGame(@PathVariable Long id) {
        return gameService.getById(id);
    }
}
