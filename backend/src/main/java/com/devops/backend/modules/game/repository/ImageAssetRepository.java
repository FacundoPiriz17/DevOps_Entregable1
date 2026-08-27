package com.devops.backend.modules.game.repository;

import com.devops.backend.modules.game.entity.ImageAsset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageAssetRepository extends JpaRepository<ImageAsset, Long> {
}
