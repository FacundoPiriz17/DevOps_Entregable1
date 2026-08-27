package com.devops.backend.modules.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "general")
public class GeneralUser {

    @Id
    @Column(name = "email_general", nullable = false)
    private String email;

    protected GeneralUser() {
    }

    public GeneralUser(String email) { this.email = email; }
    public String getEmail() { return email; }
}
