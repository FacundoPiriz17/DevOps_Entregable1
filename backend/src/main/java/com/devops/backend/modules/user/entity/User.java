package com.devops.backend.modules.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "usuario")
public class User {

    @Id
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "nombre", nullable = false)
    private String name;

    @Column(name = "pais", nullable = false)
    private String country;

    @Column(name = "activo", nullable = false)
    private boolean active = true;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate registeredAt = LocalDate.now();

    protected User() {
    }

    public User(String name, String email, String country) {
        this.name = name;
        this.email = email;
        this.country = country;
    }

    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getCountry() { return country; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDate getRegisteredAt() { return registeredAt; }
}
