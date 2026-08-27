package com.devops.backend.modules.game.service;

import com.devops.backend.common.exception.ApiException;
import com.devops.backend.modules.game.dto.GameImageResponse;
import com.devops.backend.modules.game.dto.GameRequest;
import com.devops.backend.modules.game.dto.GameResponse;
import com.devops.backend.modules.game.entity.Category;
import com.devops.backend.modules.game.entity.Game;
import com.devops.backend.modules.game.entity.GameImage;
import com.devops.backend.modules.game.entity.GameStatus;
import com.devops.backend.modules.game.entity.ImageAsset;
import com.devops.backend.modules.game.repository.CategoryRepository;
import com.devops.backend.modules.game.repository.GameImageRepository;
import com.devops.backend.modules.game.repository.GameRepository;
import com.devops.backend.modules.game.repository.ImageAssetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final CategoryRepository categoryRepository;
    private final GameImageRepository gameImageRepository;
    private final ImageAssetRepository imageAssetRepository;

    public GameService(GameRepository gameRepository,
                       CategoryRepository categoryRepository,
                       GameImageRepository gameImageRepository,
                       ImageAssetRepository imageAssetRepository) {
        this.gameRepository = gameRepository;
        this.categoryRepository = categoryRepository;
        this.gameImageRepository = gameImageRepository;
        this.imageAssetRepository = imageAssetRepository;
    }

    @Transactional(readOnly = true)
    public List<GameResponse> listAll() {
        return gameRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public GameResponse getById(Long id) {
        return toResponse(findGameOrThrow(id));
    }

    @Transactional
    public GameResponse create(GameRequest request, String adminEmail) {
        Game game = new Game(request.name().trim(), request.description().trim(), request.price(),
                request.releaseDate(), request.studio().trim(), GameStatus.fromValue(request.status()), adminEmail);
        game.replaceCategories(resolveCategories(request.categoryIds()));
        return toResponse(gameRepository.save(game));
    }

    @Transactional
    public GameResponse update(Long id, GameRequest request) {
        Game game = findGameOrThrow(id);
        game.setName(request.name().trim());
        game.setDescription(request.description().trim());
        game.setPrice(request.price());
        game.setReleaseDate(request.releaseDate());
        game.setStudio(request.studio().trim());
        game.setStatus(GameStatus.fromValue(request.status()));
        game.replaceCategories(resolveCategories(request.categoryIds()));
        return toResponse(game);
    }

    @Transactional
    public GameResponse deactivate(Long id) {
        Game game = findGameOrThrow(id);
        if (game.getStatus() == GameStatus.RETIRADO) {
            throw ApiException.conflict("GAME_ALREADY_RETIRED", "Game is already retired");
        }
        game.setStatus(GameStatus.RETIRADO);
        return toResponse(game);
    }

    @Transactional(readOnly = true)
    public Game findGameOrThrow(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("GAME_NOT_FOUND", "Game does not exist"));
    }

    private Set<Category> resolveCategories(Set<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) return Set.of();
        List<Category> categories = categoryRepository.findAllById(categoryIds);
        if (categories.size() != categoryIds.size()) {
            throw ApiException.badRequest("CATEGORY_NOT_FOUND", "One or more categories do not exist");
        }
        return new LinkedHashSet<>(categories);
    }

    private GameResponse toResponse(Game game) {
        List<GameImage> links = gameImageRepository.findByIdGameId(game.getId());
        Map<Long, ImageAsset> imagesById = imageAssetRepository
                .findAllById(links.stream().map(link -> link.getId().getImageId()).toList())
                .stream().collect(Collectors.toMap(ImageAsset::getId, Function.identity()));
        List<GameImageResponse> images = links.stream()
                .filter(link -> imagesById.containsKey(link.getId().getImageId()))
                .map(link -> GameImageResponse.from(link, imagesById.get(link.getId().getImageId())))
                .toList();
        return GameResponse.from(game, images);
    }
}
