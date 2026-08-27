package com.devops.backend.modules.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "login")
public class Login {

    @Id
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "contrasenia", nullable = false)
    private String passwordHash;

    protected Login() {
    }

    public Login(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
}
