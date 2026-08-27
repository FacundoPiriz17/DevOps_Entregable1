package com.devops.backend.modules.game.controller;

import com.devops.backend.common.config.OpenApiConfig;
import com.devops.backend.modules.game.dto.CategoryRequest;
import com.devops.backend.modules.game.dto.CategoryResponse;
import com.devops.backend.modules.game.service.CategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/categories")
@Tag(name = "Category administration", description = "Alta y eliminación de géneros y etiquetas")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class AdminCategoryController {
    private final CategoryService categoryService;
    public AdminCategoryController(CategoryService categoryService) { this.categoryService = categoryService; }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
