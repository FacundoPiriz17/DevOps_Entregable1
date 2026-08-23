package com.devops.backend.modules.game.service;

import com.devops.backend.common.exception.ApiException;
import com.devops.backend.modules.game.dto.GameRequest;
import com.devops.backend.modules.game.dto.GameResponse;
import com.devops.backend.modules.game.entity.Game;
import com.devops.backend.modules.game.entity.GameStatus;
import com.devops.backend.modules.game.repository.GameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Transactional(readOnly = true)
    public List<GameResponse> listAll() {
        return gameRepository.findAll().stream().map(GameResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public GameResponse getById(Long id) {
        return GameResponse.from(findGameOrThrow(id));
    }

    @Transactional
    public GameResponse create(GameRequest request, Long adminId) {
        Game game = new Game(request.name(), request.genre(), request.description(), adminId);
        return GameResponse.from(gameRepository.save(game));
    }

    @Transactional
    public GameResponse update(Long id, GameRequest request) {
        Game game = findGameOrThrow(id);
        game.setName(request.name());
        game.setGenre(request.genre());
        game.setDescription(request.description());
        game.touch();
        return GameResponse.from(game);
    }

    @Transactional
    public GameResponse deactivate(Long id) {
        Game game = findGameOrThrow(id);
        if (game.getStatus() == GameStatus.INACTIVE) {
            throw ApiException.conflict("GAME_ALREADY_INACTIVE", "Game is already inactive");
        }
        game.setStatus(GameStatus.INACTIVE);
        game.touch();
        return GameResponse.from(game);
    }

    Game findGameOrThrow(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("GAME_NOT_FOUND", "Game does not exist"));
    }
}
