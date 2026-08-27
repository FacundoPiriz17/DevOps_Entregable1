package com.devops.backend.modules.game.repository;

import com.devops.backend.modules.game.entity.GameImage;
import com.devops.backend.modules.game.entity.GameImageId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameImageRepository extends JpaRepository<GameImage, GameImageId> {
    List<GameImage> findByIdGameId(Long gameId);
    boolean existsByIdImageId(Long imageId);
}
