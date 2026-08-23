package com.devops.backend.user;

import com.devops.backend.common.ApiException;
import com.devops.backend.game.Game;
import com.devops.backend.game.GameRepository;
import com.devops.backend.session.GameSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class UserAdminService {

    private final UserRepository userRepository;
    private final GameSessionRepository gameSessionRepository;
    private final GameRepository gameRepository;

    public UserAdminService(UserRepository userRepository, GameSessionRepository gameSessionRepository,
                             GameRepository gameRepository) {
        this.userRepository = userRepository;
        this.gameSessionRepository = gameSessionRepository;
        this.gameRepository = gameRepository;
    }

    @Transactional(readOnly = true)
    public UserBasicResponse getUser(Long userId) {
        return UserBasicResponse.from(findUserOrThrow(userId));
    }

    @Transactional(readOnly = true)
    public List<GameUsageItem> getUsage(Long userId) {
        findUserOrThrow(userId);

        List<GameSessionRepository.GameUsageProjection> usage =
                gameSessionRepository.sumDurationMinutesGroupedByGameForUser(userId);

        Map<Long, Game> gamesById = gameRepository
                .findAllById(usage.stream().map(GameSessionRepository.GameUsageProjection::getGameId).toList())
                .stream()
                .collect(java.util.stream.Collectors.toMap(Game::getId, g -> g));

        return usage.stream()
                .map(u -> new GameUsageItem(u.getGameId(), gamesById.get(u.getGameId()).getName(), u.getTotalMinutes()))
                .toList();
    }

    @Transactional
    public UserBasicResponse deactivate(Long userId) {
        User user = findUserOrThrow(userId);
        user.setActive(false);
        return UserBasicResponse.from(user);
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("USER_NOT_FOUND", "User does not exist"));
    }
}
