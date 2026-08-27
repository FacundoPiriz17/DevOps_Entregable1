package com.devops.backend.modules.user.repository;

import com.devops.backend.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
