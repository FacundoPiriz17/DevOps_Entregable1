package com.devops.backend.modules.game.controller;

import com.devops.backend.common.config.OpenApiConfig;
import com.devops.backend.modules.game.dto.CategoryResponse;
import com.devops.backend.modules.game.service.CategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Consulta de géneros y etiquetas")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class CategoryController {
    private final CategoryService categoryService;
    public CategoryController(CategoryService categoryService) { this.categoryService = categoryService; }
    @GetMapping
    public List<CategoryResponse> listAll() { return categoryService.listAll(); }
}
