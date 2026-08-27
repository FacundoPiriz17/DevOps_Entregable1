package com.devops.backend.modules.library.service;

import com.devops.backend.common.exception.ApiException;
import com.devops.backend.modules.game.entity.Game;
import com.devops.backend.modules.game.repository.GameRepository;
import com.devops.backend.modules.library.dto.LibraryEntryResponse;
import com.devops.backend.modules.library.entity.LibraryEntry;
import com.devops.backend.modules.library.repository.LibraryEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LibraryService {

    private final LibraryEntryRepository libraryEntryRepository;
    private final GameRepository gameRepository;

    public LibraryService(LibraryEntryRepository libraryEntryRepository, GameRepository gameRepository) {
        this.libraryEntryRepository = libraryEntryRepository;
        this.gameRepository = gameRepository;
    }

    @Transactional
    public LibraryEntryResponse addToLibrary(String userEmail, Long gameId) {
        Game game = requirePurchasableGame(gameId);
        if (libraryEntryRepository.existsByIdUserEmailAndIdGameId(userEmail, gameId)) {
            throw ApiException.conflict("ALREADY_IN_LIBRARY", "Game is already in the user's library");
        }
        LibraryEntry entry = libraryEntryRepository.save(new LibraryEntry(userEmail, gameId));
        return LibraryEntryResponse.from(entry, game);
    }

    @Transactional(readOnly = true)
    public List<LibraryEntryResponse> listLibrary(String userEmail) {
        List<LibraryEntry> entries = libraryEntryRepository.findByIdUserEmail(userEmail);
        Map<Long, Game> gamesById = gameRepository.findAllById(
                        entries.stream().map(entry -> entry.getId().getGameId()).toList())
                .stream().collect(Collectors.toMap(Game::getId, Function.identity()));
        return entries.stream()
                .filter(entry -> gamesById.containsKey(entry.getId().getGameId()))
                .map(entry -> LibraryEntryResponse.from(entry, gamesById.get(entry.getId().getGameId())))
                .toList();
    }

    @Transactional
    public LibraryEntryResponse setFavorite(String userEmail, Long gameId, boolean favorite) {
        LibraryEntry entry = requireLibraryEntry(userEmail, gameId);
        entry.setFavorite(favorite);
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> ApiException.notFound("GAME_NOT_FOUND", "Game does not exist"));
        return LibraryEntryResponse.from(entry, game);
    }

    @Transactional(readOnly = true)
    public LibraryEntry requireLibraryEntry(String userEmail, Long gameId) {
        return libraryEntryRepository.findByIdUserEmailAndIdGameId(userEmail, gameId)
                .orElseThrow(() -> ApiException.forbidden(
                        "GAME_NOT_IN_LIBRARY", "Game is not available in your library"));
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
