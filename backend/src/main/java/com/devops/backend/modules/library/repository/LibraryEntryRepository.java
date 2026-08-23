package com.devops.backend.modules.library.repository;

import com.devops.backend.modules.library.entity.LibraryEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LibraryEntryRepository extends JpaRepository<LibraryEntry, Long> {

    List<LibraryEntry> findByUserId(Long userId);

    Optional<LibraryEntry> findByUserIdAndGameId(Long userId, Long gameId);

    boolean existsByUserIdAndGameId(Long userId, Long gameId);
}
