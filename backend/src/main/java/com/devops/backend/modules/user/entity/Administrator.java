package com.devops.backend.modules.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "administrador")
public class Administrator {

    @Id
    @Column(name = "email_administrador", nullable = false)
    private String email;

    protected Administrator() {
    }

    public Administrator(String email) { this.email = email; }
    public String getEmail() { return email; }
}
