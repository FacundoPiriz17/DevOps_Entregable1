package com.devops.backend.modules.game.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "juego_imagen")
public class GameImage {

    @EmbeddedId
    private GameImageId id;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "tipo", nullable = false, columnDefinition = "tipo_imagen")
    private ImageType type;

    protected GameImage() {
    }

    public GameImage(Long gameId, Long imageId, ImageType type) {
        this.id = new GameImageId(gameId, imageId);
        this.type = type;
    }

    public GameImageId getId() { return id; }
    public ImageType getType() { return type; }
}
