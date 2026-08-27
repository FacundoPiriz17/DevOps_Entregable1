package com.devops.backend.modules.game.dto;

import com.devops.backend.modules.game.entity.GameImage;
import com.devops.backend.modules.game.entity.ImageAsset;

public record GameImageResponse(Long id, String url, String alternativeText, String type) {
    public static GameImageResponse from(GameImage link, ImageAsset image) {
        return new GameImageResponse(image.getId(), image.getUrl(), image.getAlternativeText(),
                link.getType().value());
    }
}
