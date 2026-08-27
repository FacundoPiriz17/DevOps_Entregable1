package com.devops.backend.modules.game.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "imagen")
public class ImageAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imagen")
    private Long id;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "texto_alternativo")
    private String alternativeText;

    protected ImageAsset() {
    }

    public ImageAsset(String url, String alternativeText) {
        this.url = url;
        this.alternativeText = alternativeText;
    }

    public Long getId() { return id; }
    public String getUrl() { return url; }
    public String getAlternativeText() { return alternativeText; }
}
