package com.devops.backend.library;

import com.devops.backend.common.ApiException;
import com.devops.backend.game.Game;
import com.devops.backend.game.GameRepository;
import com.devops.backend.game.GameStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class LibraryService {

    private final LibraryEntryRepository libraryEntryRepository;
    private final GameRepository gameRepository;

    public LibraryService(LibraryEntryRepository libraryEntryRepository, GameRepository gameRepository) {
        this.libraryEntryRepository = libraryEntryRepository;
        this.gameRepository = gameRepository;
    }

    @Transactional
    public LibraryEntryResponse addToLibrary(Long userId, Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> ApiException.notFound("GAME_NOT_FOUND", "Game does not exist"));

        if (game.getStatus() != GameStatus.ACTIVE) {
            throw ApiException.conflict("GAME_NOT_AVAILABLE", "Game is no longer available in the store");
        }
        if (libraryEntryRepository.existsByUserIdAndGameId(userId, gameId)) {
            throw ApiException.conflict("ALREADY_IN_LIBRARY", "Game is already in the user's library");
        }

        LibraryEntry entry = libraryEntryRepository.save(new LibraryEntry(userId, gameId));
        return LibraryEntryResponse.from(entry, game);
    }

    @Transactional(readOnly = true)
    public List<LibraryEntryResponse> listLibrary(Long userId) {
        List<LibraryEntry> entries = libraryEntryRepository.findByUserId(userId);
        Map<Long, Game> gamesById = gameRepository.findAllById(entries.stream().map(LibraryEntry::getGameId).toList())
                .stream()
                .collect(java.util.stream.Collectors.toMap(Game::getId, Function.identity()));

        return entries.stream()
                .map(entry -> LibraryEntryResponse.from(entry, gamesById.get(entry.getGameId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public LibraryEntry requireLibraryEntry(Long userId, Long gameId) {
        return libraryEntryRepository.findByUserIdAndGameId(userId, gameId)
                .orElseThrow(() -> ApiException.forbidden("GAME_NOT_IN_LIBRARY", "Game is not available in your library"));
    }
}
