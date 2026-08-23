package com.devops.backend.modules.session.service;

import com.devops.backend.common.exception.ApiException;
import com.devops.backend.modules.library.service.LibraryService;
import com.devops.backend.modules.session.dto.PlaytimeResponse;
import com.devops.backend.modules.session.dto.SessionResponse;
import com.devops.backend.modules.session.entity.GameSession;
import com.devops.backend.modules.session.entity.SessionStatus;
import com.devops.backend.modules.session.repository.GameSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessionService {

    private final GameSessionRepository gameSessionRepository;
    private final LibraryService libraryService;

    public SessionService(GameSessionRepository gameSessionRepository, LibraryService libraryService) {
        this.gameSessionRepository = gameSessionRepository;
        this.libraryService = libraryService;
    }

    @Transactional
    public SessionResponse start(Long userId, Long gameId) {
        libraryService.requireLibraryEntry(userId, gameId);

        gameSessionRepository.findByUserIdAndGameIdAndStatus(userId, gameId, SessionStatus.ACTIVE)
                .ifPresent(s -> {
                    throw ApiException.conflict("SESSION_ALREADY_ACTIVE", "A session is already active for this game");
                });

        GameSession session = gameSessionRepository.save(new GameSession(userId, gameId));
        return SessionResponse.from(session);
    }

    @Transactional
    public SessionResponse stop(Long userId, Long gameId) {
        GameSession session = gameSessionRepository.findByUserIdAndGameIdAndStatus(userId, gameId, SessionStatus.ACTIVE)
                .orElseThrow(() -> ApiException.conflict("NO_ACTIVE_SESSION", "There is no active session for this game"));

        session.finish();
        return SessionResponse.from(session);
    }

    @Transactional(readOnly = true)
    public SessionResponse getSession(Long userId, Long sessionId) {
        GameSession session = gameSessionRepository.findById(sessionId)
                .filter(s -> s.getUserId().equals(userId))
                .orElseThrow(() -> ApiException.notFound("SESSION_NOT_FOUND", "Session does not exist"));
        return SessionResponse.from(session);
    }

    @Transactional(readOnly = true)
    public PlaytimeResponse getTotalPlaytime(Long userId, Long gameId) {
        libraryService.requireLibraryEntry(userId, gameId);
        long total = gameSessionRepository.sumDurationMinutesByUserIdAndGameId(userId, gameId);
        return new PlaytimeResponse(gameId, total);
    }
}
