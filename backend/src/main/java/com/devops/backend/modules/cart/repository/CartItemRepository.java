package com.devops.backend.modules.cart.repository;

import com.devops.backend.modules.cart.entity.CartItem;
import com.devops.backend.modules.cart.entity.CartItemId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, CartItemId> {
    List<CartItem> findByIdUserEmail(String userEmail);
    boolean existsByIdUserEmailAndIdGameId(String userEmail, Long gameId);
}
