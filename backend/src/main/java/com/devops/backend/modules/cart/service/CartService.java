package com.devops.backend.modules.cart.service;

import com.devops.backend.common.exception.ApiException;
import com.devops.backend.modules.cart.dto.CartItemResponse;
import com.devops.backend.modules.cart.entity.CartItem;
import com.devops.backend.modules.cart.entity.CartItemId;
import com.devops.backend.modules.cart.repository.CartItemRepository;
import com.devops.backend.modules.game.entity.Game;
import com.devops.backend.modules.game.repository.GameRepository;
import com.devops.backend.modules.library.dto.LibraryEntryResponse;
import com.devops.backend.modules.library.repository.LibraryEntryRepository;
import com.devops.backend.modules.library.service.LibraryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final GameRepository gameRepository;
    private final LibraryEntryRepository libraryEntryRepository;
    private final LibraryService libraryService;

    public CartService(CartItemRepository cartItemRepository,
                       GameRepository gameRepository,
                       LibraryEntryRepository libraryEntryRepository,
                       LibraryService libraryService) {
        this.cartItemRepository = cartItemRepository;
        this.gameRepository = gameRepository;
        this.libraryEntryRepository = libraryEntryRepository;
        this.libraryService = libraryService;
    }

    @Transactional
    public CartItemResponse add(String userEmail, Long gameId) {
        Game game = requirePurchasableGame(gameId);
        if (libraryEntryRepository.existsByIdUserEmailAndIdGameId(userEmail, gameId)) {
            throw ApiException.conflict("ALREADY_IN_LIBRARY", "Game is already in the user's library");
        }
        if (cartItemRepository.existsByIdUserEmailAndIdGameId(userEmail, gameId)) {
            throw ApiException.conflict("ALREADY_IN_CART", "Game is already in the cart");
        }
        cartItemRepository.save(new CartItem(userEmail, gameId));
        return CartItemResponse.from(game);
    }

    @Transactional(readOnly = true)
    public List<CartItemResponse> list(String userEmail) {
        List<CartItem> items = cartItemRepository.findByIdUserEmail(userEmail);
        Map<Long, Game> games = gameRepository.findAllById(
                        items.stream().map(item -> item.getId().getGameId()).toList())
                .stream().collect(Collectors.toMap(Game::getId, Function.identity()));
        return items.stream().map(item -> games.get(item.getId().getGameId()))
                .filter(java.util.Objects::nonNull).map(CartItemResponse::from).toList();
    }

    @Transactional
    public void remove(String userEmail, Long gameId) {
        CartItemId id = new CartItemId(gameId, userEmail);
        if (!cartItemRepository.existsById(id)) {
            throw ApiException.notFound("CART_ITEM_NOT_FOUND", "Game is not in the cart");
        }
        cartItemRepository.deleteById(id);
    }

    @Transactional
    public List<LibraryEntryResponse> checkout(String userEmail) {
        List<CartItem> items = cartItemRepository.findByIdUserEmail(userEmail);
        if (items.isEmpty()) throw ApiException.badRequest("EMPTY_CART", "The cart is empty");
        List<LibraryEntryResponse> purchased = items.stream()
                .map(item -> libraryService.addToLibrary(userEmail, item.getId().getGameId()))
                .toList();
        cartItemRepository.deleteAll(items);
        return purchased;
    }

    private Game requirePurchasableGame(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> ApiException.notFound("GAME_NOT_FOUND", "Game does not exist"));
        if (!game.getStatus().isPurchasable()) {
            throw ApiException.conflict("GAME_NOT_AVAILABLE", "Game is not available for purchase");
        }
        return game;
    }
}
