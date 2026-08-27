package com.devops.backend.modules.game.service;

import com.devops.backend.common.exception.ApiException;
import com.devops.backend.modules.game.dto.GameImageRequest;
import com.devops.backend.modules.game.dto.GameImageResponse;
import com.devops.backend.modules.game.entity.Game;
import com.devops.backend.modules.game.entity.GameImage;
import com.devops.backend.modules.game.entity.GameImageId;
import com.devops.backend.modules.game.entity.ImageAsset;
import com.devops.backend.modules.game.entity.ImageType;
import com.devops.backend.modules.game.repository.GameImageRepository;
import com.devops.backend.modules.game.repository.ImageAssetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GameImageService {

    private final GameService gameService;
    private final ImageAssetRepository imageAssetRepository;
    private final GameImageRepository gameImageRepository;

    public GameImageService(GameService gameService,
                            ImageAssetRepository imageAssetRepository,
                            GameImageRepository gameImageRepository) {
        this.gameService = gameService;
        this.imageAssetRepository = imageAssetRepository;
        this.gameImageRepository = gameImageRepository;
    }

    @Transactional
    public GameImageResponse add(Long gameId, GameImageRequest request) {
        Game game = gameService.findGameOrThrow(gameId);
        ImageAsset image = imageAssetRepository.save(
                new ImageAsset(request.url().trim(), normalizeAlternativeText(request.alternativeText())));
        GameImage link = gameImageRepository.save(
                new GameImage(game.getId(), image.getId(), ImageType.fromValue(request.type())));
        return GameImageResponse.from(link, image);
    }

    @Transactional
    public void remove(Long gameId, Long imageId) {
        GameImageId id = new GameImageId(gameId, imageId);
        if (!gameImageRepository.existsById(id)) {
            throw ApiException.notFound("GAME_IMAGE_NOT_FOUND", "Image is not associated with this game");
        }
        gameImageRepository.deleteById(id);
        gameImageRepository.flush();
        if (!gameImageRepository.existsByIdImageId(imageId)) imageAssetRepository.deleteById(imageId);
    }

    private String normalizeAlternativeText(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
