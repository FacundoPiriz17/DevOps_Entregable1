package com.devops.backend.library;

import com.devops.backend.common.ApiException;
import com.devops.backend.game.Game;
import com.devops.backend.game.GameRepository;
import com.devops.backend.game.GameStatus;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @Mock
    private LibraryEntryRepository libraryEntryRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private LibraryService libraryService;

    @Test
    void addToLibrary_activeGameNotYetAdded_addsEntry() throws Exception {
        Game game = new Game("Death Stranding", "Aventura", "desc", 1L);
        setId(game, 10L);
        when(gameRepository.findById(10L)).thenReturn(Optional.of(game));
        when(libraryEntryRepository.existsByUserIdAndGameId(1L, 10L)).thenReturn(false);
        when(libraryEntryRepository.save(any(LibraryEntry.class))).thenAnswer(inv -> inv.getArgument(0));

        LibraryEntryResponse response = libraryService.addToLibrary(1L, 10L);

        assertThat(response.gameId()).isEqualTo(10L);
        assertThat(response.name()).isEqualTo("Death Stranding");
    }

    @Test
    void addToLibrary_gameNotFound_throwsNotFound() {
        when(gameRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> libraryService.addToLibrary(1L, 10L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("does not exist");
    }

    @Test
    void addToLibrary_inactiveGame_throwsConflict() {
        Game game = new Game("Old Game", "Genre", "desc", 1L);
        game.setStatus(GameStatus.INACTIVE);
        when(gameRepository.findById(10L)).thenReturn(Optional.of(game));

        assertThatThrownBy(() -> libraryService.addToLibrary(1L, 10L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("no longer available");
    }

    @Test
    void addToLibrary_alreadyInLibrary_throwsConflict() {
        Game game = new Game("Death Stranding", "Aventura", "desc", 1L);
        when(gameRepository.findById(10L)).thenReturn(Optional.of(game));
        when(libraryEntryRepository.existsByUserIdAndGameId(1L, 10L)).thenReturn(true);

        assertThatThrownBy(() -> libraryService.addToLibrary(1L, 10L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("already in the user's library");
    }

    @Test
    void listLibrary_returnsEntriesJoinedWithGameInfo() throws Exception {
        LibraryEntry entry = new LibraryEntry(1L, 10L);
        Game game = new Game("Death Stranding", "Aventura", "desc", 1L);
        setId(game, 10L);
        when(libraryEntryRepository.findByUserId(1L)).thenReturn(List.of(entry));
        when(gameRepository.findAllById(List.of(10L))).thenReturn(List.of(game));

        List<LibraryEntryResponse> library = libraryService.listLibrary(1L);

        assertThat(library).hasSize(1);
        assertThat(library.get(0).name()).isEqualTo("Death Stranding");
    }

    @Test
    void requireLibraryEntry_notInLibrary_throwsForbidden() {
        when(libraryEntryRepository.findByUserIdAndGameId(1L, 10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> libraryService.requireLibraryEntry(1L, 10L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("not available in your library");
    }

    private static void setId(Game game, Long id) throws Exception {
        Field field = Game.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(game, id);
    }
}
