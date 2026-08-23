package com.devops.backend.modules.game.service;

import com.devops.backend.common.exception.ApiException;
import com.devops.backend.modules.game.dto.GameRequest;
import com.devops.backend.modules.game.dto.GameResponse;
import com.devops.backend.modules.game.entity.Game;
import com.devops.backend.modules.game.entity.GameStatus;
import com.devops.backend.modules.game.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    @Test
    void create_savesGameRegisteredByAdmin() {
        Game game = new Game("Death Stranding", "Aventura", "Un juego de entregas", 1L);
        when(gameRepository.save(any(Game.class))).thenReturn(game);

        GameResponse response = gameService.create(new GameRequest("Death Stranding", "Aventura", "Un juego de entregas"), 1L);

        assertThat(response.name()).isEqualTo("Death Stranding");
        assertThat(response.status()).isEqualTo("ACTIVE");
        assertThat(response.available()).isTrue();
    }

    @Test
    void listAll_marksInactiveGamesAsUnavailable() {
        Game active = new Game("Active Game", "RPG", "desc", 1L);
        Game inactive = new Game("Inactive Game", "RPG", "desc", 1L);
        inactive.setStatus(GameStatus.INACTIVE);
        when(gameRepository.findAll()).thenReturn(List.of(active, inactive));

        List<GameResponse> games = gameService.listAll();

        assertThat(games).hasSize(2);
        assertThat(games).anySatisfy(g -> {
            if (g.name().equals("Active Game")) {
                assertThat(g.available()).isTrue();
            }
        });
        assertThat(games.stream().filter(g -> g.name().equals("Inactive Game")).findFirst().orElseThrow().available())
                .isFalse();
    }

    @Test
    void update_existingGame_updatesFields() {
        Game game = new Game("Old Name", "Old Genre", "Old description", 1L);
        when(gameRepository.findById(5L)).thenReturn(Optional.of(game));

        GameResponse response = gameService.update(5L, new GameRequest("New Name", "New Genre", "New description"));

        assertThat(response.name()).isEqualTo("New Name");
        assertThat(response.genre()).isEqualTo("New Genre");
        assertThat(response.description()).isEqualTo("New description");
    }

    @Test
    void update_nonExistingGame_throwsNotFound() {
        when(gameRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gameService.update(99L, new GameRequest("a", "b", "c")))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("does not exist");
    }

    @Test
    void deactivate_activeGame_marksInactive() {
        Game game = new Game("Some Game", "Genre", "desc", 1L);
        when(gameRepository.findById(5L)).thenReturn(Optional.of(game));

        GameResponse response = gameService.deactivate(5L);

        assertThat(response.status()).isEqualTo("INACTIVE");
        assertThat(response.available()).isFalse();
    }

    @Test
    void deactivate_alreadyInactiveGame_throwsConflict() {
        Game game = new Game("Some Game", "Genre", "desc", 1L);
        game.setStatus(GameStatus.INACTIVE);
        when(gameRepository.findById(5L)).thenReturn(Optional.of(game));

        assertThatThrownBy(() -> gameService.deactivate(5L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("already inactive");
    }
}
