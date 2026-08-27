package com.devops.backend.modules.game.repository;

import com.devops.backend.modules.game.entity.Category;
import com.devops.backend.modules.game.entity.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameIgnoreCaseAndType(String name, CategoryType type);
}
