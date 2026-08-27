package com.devops.backend.modules.game.service;

import com.devops.backend.common.exception.ApiException;
import com.devops.backend.modules.game.dto.CategoryRequest;
import com.devops.backend.modules.game.dto.CategoryResponse;
import com.devops.backend.modules.game.entity.Category;
import com.devops.backend.modules.game.entity.CategoryType;
import com.devops.backend.modules.game.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> listAll() {
        return categoryRepository.findAll().stream().map(CategoryResponse::from).toList();
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        CategoryType type = CategoryType.fromValue(request.type());
        String name = request.name().trim();
        if (categoryRepository.existsByNameIgnoreCaseAndType(name, type)) {
            throw ApiException.conflict("CATEGORY_ALREADY_EXISTS", "Category already exists");
        }
        return CategoryResponse.from(categoryRepository.save(new Category(name, type)));
    }

    @Transactional
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw ApiException.notFound("CATEGORY_NOT_FOUND", "Category does not exist");
        }
        categoryRepository.deleteById(id);
    }
}
