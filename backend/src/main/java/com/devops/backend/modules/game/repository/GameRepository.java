package com.devops.backend.modules.game.repository;

import com.devops.backend.modules.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
}
