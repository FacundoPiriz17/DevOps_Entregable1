package com.devops.backend.modules.wishlist.service;

import com.devops.backend.common.exception.ApiException;
import com.devops.backend.modules.game.entity.Game;
import com.devops.backend.modules.game.entity.GameStatus;
import com.devops.backend.modules.game.repository.GameRepository;
import com.devops.backend.modules.library.repository.LibraryEntryRepository;
import com.devops.backend.modules.wishlist.dto.WishlistItemResponse;
import com.devops.backend.modules.wishlist.entity.WishlistItem;
import com.devops.backend.modules.wishlist.entity.WishlistItemId;
import com.devops.backend.modules.wishlist.repository.WishlistItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WishlistService {
    private final WishlistItemRepository wishlistItemRepository;
    private final LibraryEntryRepository libraryEntryRepository;
    private final GameRepository gameRepository;

    public WishlistService(WishlistItemRepository wishlistItemRepository,
                           LibraryEntryRepository libraryEntryRepository,
                           GameRepository gameRepository) {
        this.wishlistItemRepository = wishlistItemRepository;
        this.libraryEntryRepository = libraryEntryRepository;
        this.gameRepository = gameRepository;
    }

    @Transactional
    public WishlistItemResponse add(String userEmail, Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> ApiException.notFound("GAME_NOT_FOUND", "Game does not exist"));
        if (game.getStatus() == GameStatus.RETIRADO) {
            throw ApiException.conflict("GAME_RETIRED", "Retired games cannot be added to the wishlist");
        }
        if (libraryEntryRepository.existsByIdUserEmailAndIdGameId(userEmail, gameId)) {
            throw ApiException.conflict("ALREADY_IN_LIBRARY", "Game is already in the user's library");
        }
        if (wishlistItemRepository.existsByIdUserEmailAndIdGameId(userEmail, gameId)) {
            throw ApiException.conflict("ALREADY_IN_WISHLIST", "Game is already in the wishlist");
        }
        WishlistItem item = wishlistItemRepository.save(new WishlistItem(userEmail, gameId));
        return WishlistItemResponse.from(item, game);
    }

    @Transactional(readOnly = true)
    public List<WishlistItemResponse> list(String userEmail) {
        List<WishlistItem> items = wishlistItemRepository.findByIdUserEmail(userEmail);
        Map<Long, Game> games = gameRepository.findAllById(
                        items.stream().map(item -> item.getId().getGameId()).toList())
                .stream().collect(Collectors.toMap(Game::getId, Function.identity()));
        return items.stream().filter(item -> games.containsKey(item.getId().getGameId()))
                .map(item -> WishlistItemResponse.from(item, games.get(item.getId().getGameId())))
                .toList();
    }

    @Transactional
    public void remove(String userEmail, Long gameId) {
        WishlistItemId id = new WishlistItemId(gameId, userEmail);
        if (!wishlistItemRepository.existsById(id)) {
            throw ApiException.notFound("WISHLIST_ITEM_NOT_FOUND", "Game is not in the wishlist");
        }
        wishlistItemRepository.deleteById(id);
    }
}
