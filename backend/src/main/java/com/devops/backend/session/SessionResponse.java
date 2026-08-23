package com.devops.backend.session;

import java.time.Instant;

public record SessionResponse(
        Long id,
        Long gameId,
        Instant startedAt,
        Instant endedAt,
        Long durationMinutes,
        String status) {

    public static SessionResponse from(GameSession session) {
        return new SessionResponse(
                session.getId(),
                session.getGameId(),
                session.getStartedAt(),
                session.getEndedAt(),
                session.getDurationMinutes(),
                session.getStatus().name());
    }
}
