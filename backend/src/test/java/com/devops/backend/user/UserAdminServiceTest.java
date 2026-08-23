package com.devops.backend.user;

import com.devops.backend.common.ApiException;
import com.devops.backend.game.Game;
import com.devops.backend.game.GameRepository;
import com.devops.backend.session.GameSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameSessionRepository gameSessionRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private UserAdminService userAdminService;

    @Test
    void getUser_existingUser_returnsBasicInfo() {
        User user = new User("Julia Fernandez", "julia@example.com", "hashed", Role.USER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserBasicResponse response = userAdminService.getUser(1L);

        assertThat(response.name()).isEqualTo("Julia Fernandez");
        assertThat(response.email()).isEqualTo("julia@example.com");
        assertThat(response.active()).isTrue();
    }

    @Test
    void getUser_nonExistingUser_throwsNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userAdminService.getUser(1L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("does not exist");
    }

    @Test
    void getUsage_returnsPlaytimePerGame() throws Exception {
        User user = new User("Julia Fernandez", "julia@example.com", "hashed", Role.USER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        GameSessionRepository.GameUsageProjection projection = mockProjection(10L, 165L);
        when(gameSessionRepository.sumDurationMinutesGroupedByGameForUser(1L)).thenReturn(List.of(projection));

        Game game = new Game("Death Stranding", "Aventura", "desc", 1L);
        setGameId(game, 10L);
        when(gameRepository.findAllById(List.of(10L))).thenReturn(List.of(game));

        List<GameUsageItem> usage = userAdminService.getUsage(1L);

        assertThat(usage).hasSize(1);
        assertThat(usage.get(0).gameId()).isEqualTo(10L);
        assertThat(usage.get(0).gameName()).isEqualTo("Death Stranding");
        assertThat(usage.get(0).totalMinutes()).isEqualTo(165L);
    }

    @Test
    void deactivate_activeUser_marksInactive() {
        User user = new User("Julia Fernandez", "julia@example.com", "hashed", Role.USER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserBasicResponse response = userAdminService.deactivate(1L);

        assertThat(response.active()).isFalse();
    }

    private static GameSessionRepository.GameUsageProjection mockProjection(Long gameId, Long totalMinutes) {
        GameSessionRepository.GameUsageProjection projection =
                org.mockito.Mockito.mock(GameSessionRepository.GameUsageProjection.class);
        when(projection.getGameId()).thenReturn(gameId);
        when(projection.getTotalMinutes()).thenReturn(totalMinutes);
        return projection;
    }

    private static void setGameId(Game game, Long id) throws Exception {
        Field field = Game.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(game, id);
    }
}
