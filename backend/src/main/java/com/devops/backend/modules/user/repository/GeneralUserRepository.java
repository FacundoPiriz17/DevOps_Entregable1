package com.devops.backend.modules.user.repository;

import com.devops.backend.modules.user.entity.GeneralUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeneralUserRepository extends JpaRepository<GeneralUser, String> {
}
