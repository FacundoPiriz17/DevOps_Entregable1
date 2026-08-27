package com.devops.backend.modules.auth.repository;

import com.devops.backend.modules.auth.entity.Login;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<Login, String> {
}
