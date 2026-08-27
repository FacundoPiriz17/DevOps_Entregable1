package com.devops.backend.modules.user.repository;

import com.devops.backend.modules.user.entity.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministratorRepository extends JpaRepository<Administrator, String> {
}
