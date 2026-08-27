package com.devops.backend.modules.cart.controller;

import com.devops.backend.common.config.OpenApiConfig;
import com.devops.backend.common.security.CurrentUser;
import com.devops.backend.modules.cart.dto.CartItemResponse;
import com.devops.backend.modules.cart.service.CartService;
import com.devops.backend.modules.library.dto.LibraryEntryResponse;
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
@RequestMapping("/api/cart")
@Tag(name = "Cart", description = "Gestión del carrito y compra de sus juegos")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class CartController {
    private final CartService cartService;
    private final CurrentUser currentUser;
    public CartController(CartService cartService, CurrentUser currentUser) {
        this.cartService = cartService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public List<CartItemResponse> list(JwtAuthenticationToken auth) {
        return cartService.list(currentUser.emailFrom(auth));
    }

    @PostMapping("/games/{gameId}")
    public ResponseEntity<CartItemResponse> add(@PathVariable Long gameId, JwtAuthenticationToken auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cartService.add(currentUser.emailFrom(auth), gameId));
    }

    @DeleteMapping("/games/{gameId}")
    public ResponseEntity<Void> remove(@PathVariable Long gameId, JwtAuthenticationToken auth) {
        cartService.remove(currentUser.emailFrom(auth), gameId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/checkout")
    public List<LibraryEntryResponse> checkout(JwtAuthenticationToken auth) {
        return cartService.checkout(currentUser.emailFrom(auth));
    }
}
