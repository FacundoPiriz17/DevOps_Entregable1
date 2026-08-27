package com.devops.backend.modules.wishlist.repository;

import com.devops.backend.modules.wishlist.entity.WishlistItem;
import com.devops.backend.modules.wishlist.entity.WishlistItemId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, WishlistItemId> {
    List<WishlistItem> findByIdUserEmail(String userEmail);
    boolean existsByIdUserEmailAndIdGameId(String userEmail, Long gameId);
}
