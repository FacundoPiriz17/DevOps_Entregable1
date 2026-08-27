package com.devops.backend.modules.library.repository;

import com.devops.backend.modules.library.entity.LibraryEntry;
import com.devops.backend.modules.library.entity.LibraryEntryId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LibraryEntryRepository extends JpaRepository<LibraryEntry, LibraryEntryId> {
    List<LibraryEntry> findByIdUserEmail(String userEmail);
    Optional<LibraryEntry> findByIdUserEmailAndIdGameId(String userEmail, Long gameId);
    boolean existsByIdUserEmailAndIdGameId(String userEmail, Long gameId);
}
