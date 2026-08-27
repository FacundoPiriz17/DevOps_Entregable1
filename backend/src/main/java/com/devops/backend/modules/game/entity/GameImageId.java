package com.devops.backend.modules.game.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class GameImageId implements Serializable {

    @Column(name = "identificador_juego")
    private Long gameId;

    @Column(name = "id_imagen")
    private Long imageId;

    protected GameImageId() {
    }

    public GameImageId(Long gameId, Long imageId) {
        this.gameId = gameId;
        this.imageId = imageId;
    }

    public Long getGameId() { return gameId; }
    public Long getImageId() { return imageId; }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof GameImageId that)) return false;
        return Objects.equals(gameId, that.gameId) && Objects.equals(imageId, that.imageId);
    }

    @Override
    public int hashCode() { return Objects.hash(gameId, imageId); }
}
