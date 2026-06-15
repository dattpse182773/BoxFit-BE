package com.boxfit.backend.repository;

import com.boxfit.backend.domain.entity.CartItem;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    Optional<CartItem> findByCartIdAndVariantId(UUID cartId, UUID variantId);
    Optional<CartItem> findByIdAndCartUserId(UUID cartItemId, UUID userId);
    void deleteByCartId(UUID cartId);
}
