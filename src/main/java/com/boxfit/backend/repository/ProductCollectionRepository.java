package com.boxfit.backend.repository;

import com.boxfit.backend.domain.entity.ProductCollection;
import com.boxfit.backend.domain.enums.CollectionStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCollectionRepository extends JpaRepository<ProductCollection, UUID> {
    boolean existsBySlug(String slug);
    boolean existsBySlugAndIdNot(String slug, UUID id);

    @EntityGraph(attributePaths = {"products"})
    Optional<ProductCollection> findBySlug(String slug);

    @EntityGraph(attributePaths = {"products"})
    Optional<ProductCollection> findBySlugAndStatus(String slug, CollectionStatus status);

    @EntityGraph(attributePaths = {"products"})
    Page<ProductCollection> findByStatus(CollectionStatus status, Pageable pageable);
}
