package com.boxfit.backend.repository;

import com.boxfit.backend.domain.entity.ProductVariant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {
    List<ProductVariant> findByProduct_IdAndIsActiveTrueOrderByCreatedAtAsc(UUID productId);
    List<ProductVariant> findByProduct_SlugAndIsActiveTrueOrderByCreatedAtAsc(String productSlug);
    Optional<ProductVariant> findByIdAndIsActiveTrue(UUID id);
    boolean existsBySku(String sku);
    boolean existsBySkuAndIdNot(String sku, UUID id);
}

