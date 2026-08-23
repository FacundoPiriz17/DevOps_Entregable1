package com.devops.backend.modules.session.repository;

import com.devops.backend.modules.session.entity.GameSession;
import com.devops.backend.modules.session.entity.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {

    Optional<GameSession> findByUserIdAndGameIdAndStatus(Long userId, Long gameId, SessionStatus status);

    @Query("select coalesce(sum(s.durationMinutes), 0) from GameSession s "
            + "where s.userId = :userId and s.gameId = :gameId and s.status = com.devops.backend.modules.session.entity.SessionStatus.FINISHED")
    Long sumDurationMinutesByUserIdAndGameId(@Param("userId") Long userId, @Param("gameId") Long gameId);

    @Query("select s.gameId as gameId, coalesce(sum(s.durationMinutes), 0) as totalMinutes from GameSession s "
            + "where s.userId = :userId and s.status = com.devops.backend.modules.session.entity.SessionStatus.FINISHED "
            + "group by s.gameId")
    List<GameUsageProjection> sumDurationMinutesGroupedByGameForUser(@Param("userId") Long userId);

    interface GameUsageProjection {
        Long getGameId();
        Long getTotalMinutes();
    }
}
