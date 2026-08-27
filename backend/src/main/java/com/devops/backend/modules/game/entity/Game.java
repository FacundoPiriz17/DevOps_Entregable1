package com.devops.backend.modules.game.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "juego")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "identificador")
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String name;

    @Column(name = "descripcion", nullable = false)
    private String description;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "fecha_lanzamiento", nullable = false)
    private LocalDate releaseDate;

    @Column(name = "estudio", nullable = false)
    private String studio;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "estado", nullable = false, columnDefinition = "estado_juego")
    private GameStatus status = GameStatus.PUBLICADO;

    @Column(name = "admin_registra", nullable = false)
    private String registeredByAdminEmail;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate registeredAt = LocalDate.now();

    @ManyToMany
    @JoinTable(name = "juego_categoria",
            joinColumns = @JoinColumn(name = "identificador_juego"),
            inverseJoinColumns = @JoinColumn(name = "identificador_categoria"))
    private Set<Category> categories = new LinkedHashSet<>();

    protected Game() {
    }

    public Game(String name, String description, BigDecimal price, LocalDate releaseDate,
                String studio, GameStatus status, String registeredByAdminEmail) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.releaseDate = releaseDate;
        this.studio = studio;
        this.status = status;
        this.registeredByAdminEmail = registeredByAdminEmail;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }
    public String getStudio() { return studio; }
    public void setStudio(String studio) { this.studio = studio; }
    public GameStatus getStatus() { return status; }
    public void setStatus(GameStatus status) { this.status = status; }
    public String getRegisteredByAdminEmail() { return registeredByAdminEmail; }
    public LocalDate getRegisteredAt() { return registeredAt; }
    public Set<Category> getCategories() { return categories; }
    public void replaceCategories(Set<Category> categories) {
        this.categories.clear();
        this.categories.addAll(categories);
    }
}
