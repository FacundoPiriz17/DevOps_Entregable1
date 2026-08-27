package com.devops.backend.modules.wishlist.controller;

import com.devops.backend.common.config.OpenApiConfig;
import com.devops.backend.common.security.CurrentUser;
import com.devops.backend.modules.wishlist.dto.WishlistItemResponse;
import com.devops.backend.modules.wishlist.service.WishlistService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@Tag(name = "Wishlist", description = "Gestión de la lista personal de deseados")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class WishlistController {
    private final WishlistService wishlistService;
    private final CurrentUser currentUser;
    public WishlistController(WishlistService wishlistService, CurrentUser currentUser) {
        this.wishlistService = wishlistService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public List<WishlistItemResponse> list(JwtAuthenticationToken auth) {
        return wishlistService.list(currentUser.emailFrom(auth));
    }

    @PostMapping("/games/{gameId}")
    public ResponseEntity<WishlistItemResponse> add(@PathVariable Long gameId, JwtAuthenticationToken auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(wishlistService.add(currentUser.emailFrom(auth), gameId));
    }

    @DeleteMapping("/games/{gameId}")
    public ResponseEntity<Void> remove(@PathVariable Long gameId, JwtAuthenticationToken auth) {
        wishlistService.remove(currentUser.emailFrom(auth), gameId);
        return ResponseEntity.noContent().build();
    }
}
