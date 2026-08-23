package com.devops.backend.modules.session.service;

import com.devops.backend.common.exception.ApiException;
import com.devops.backend.modules.library.entity.LibraryEntry;
import com.devops.backend.modules.library.service.LibraryService;
import com.devops.backend.modules.session.dto.PlaytimeResponse;
import com.devops.backend.modules.session.dto.SessionResponse;
import com.devops.backend.modules.session.entity.GameSession;
import com.devops.backend.modules.session.entity.SessionStatus;
import com.devops.backend.modules.session.repository.GameSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private GameSessionRepository gameSessionRepository;

    @Mock
    private LibraryService libraryService;

    @InjectMocks
    private SessionService sessionService;

    @Test
    void start_gameInLibraryWithoutActiveSession_createsSession() {
        when(libraryService.requireLibraryEntry(1L, 10L)).thenReturn(new LibraryEntry(1L, 10L));
        when(gameSessionRepository.findByUserIdAndGameIdAndStatus(1L, 10L, SessionStatus.ACTIVE))
                .thenReturn(Optional.empty());
        when(gameSessionRepository.save(any(GameSession.class))).thenAnswer(inv -> inv.getArgument(0));

        SessionResponse response = sessionService.start(1L, 10L);

        assertThat(response.status()).isEqualTo("ACTIVE");
        assertThat(response.gameId()).isEqualTo(10L);
    }

    @Test
    void start_gameNotInLibrary_propagatesForbidden() {
        when(libraryService.requireLibraryEntry(1L, 10L))
                .thenThrow(ApiException.forbidden("GAME_NOT_IN_LIBRARY", "Game is not available in your library"));

        assertThatThrownBy(() -> sessionService.start(1L, 10L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("not available in your library");
    }

    @Test
    void start_sessionAlreadyActive_throwsConflict() {
        when(libraryService.requireLibraryEntry(1L, 10L)).thenReturn(new LibraryEntry(1L, 10L));
        when(gameSessionRepository.findByUserIdAndGameIdAndStatus(1L, 10L, SessionStatus.ACTIVE))
                .thenReturn(Optional.of(new GameSession(1L, 10L)));

        assertThatThrownBy(() -> sessionService.start(1L, 10L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("already active");
    }

    @Test
    void stop_activeSession_finishesAndComputesDuration() throws Exception {
        GameSession session = new GameSession(1L, 10L);
        setStartedAt(session, Instant.now().minus(45, ChronoUnit.MINUTES));
        when(gameSessionRepository.findByUserIdAndGameIdAndStatus(1L, 10L, SessionStatus.ACTIVE))
                .thenReturn(Optional.of(session));

        SessionResponse response = sessionService.stop(1L, 10L);

        assertThat(response.status()).isEqualTo("FINISHED");
        assertThat(response.durationMinutes()).isGreaterThanOrEqualTo(44L);
        assertThat(response.endedAt()).isNotNull();
    }

    @Test
    void stop_noActiveSession_throwsConflict() {
        when(gameSessionRepository.findByUserIdAndGameIdAndStatus(1L, 10L, SessionStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.stop(1L, 10L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("no active session");
    }

    @Test
    void getSession_ownedByRequestingUser_returnsSession() throws Exception {
        GameSession session = new GameSession(1L, 10L);
        setId(session, 99L);
        when(gameSessionRepository.findById(99L)).thenReturn(Optional.of(session));

        SessionResponse response = sessionService.getSession(1L, 99L);

        assertThat(response.id()).isEqualTo(99L);
    }

    @Test
    void getSession_ownedByAnotherUser_throwsNotFound() throws Exception {
        GameSession session = new GameSession(2L, 10L);
        setId(session, 99L);
        when(gameSessionRepository.findById(99L)).thenReturn(Optional.of(session));

        assertThatThrownBy(() -> sessionService.getSession(1L, 99L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("does not exist");
    }

    @Test
    void getTotalPlaytime_sumsFinishedSessions() {
        when(libraryService.requireLibraryEntry(1L, 10L)).thenReturn(new LibraryEntry(1L, 10L));
        when(gameSessionRepository.sumDurationMinutesByUserIdAndGameId(1L, 10L)).thenReturn(165L);

        PlaytimeResponse response = sessionService.getTotalPlaytime(1L, 10L);

        assertThat(response.totalMinutes()).isEqualTo(165L);
        assertThat(response.gameId()).isEqualTo(10L);
    }

    private static void setStartedAt(GameSession session, Instant startedAt) throws Exception {
        Field field = GameSession.class.getDeclaredField("startedAt");
        field.setAccessible(true);
        field.set(session, startedAt);
    }

    private static void setId(GameSession session, Long id) throws Exception {
        Field field = GameSession.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(session, id);
    }
}
