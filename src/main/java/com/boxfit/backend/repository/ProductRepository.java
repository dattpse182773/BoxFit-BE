package com.boxfit.backend.repository;

import com.boxfit.backend.domain.entity.Product;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    Optional<Product> findBySlug(String slug);
    boolean existsBySlug(String slug);
    boolean existsBySlugAndIdNot(String slug, UUID id);

    @EntityGraph(attributePaths = {"category"})
    Page<Product> findAllByIsActiveTrue(Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Optional<Product> findBySlugAndIsActiveTrue(String slug);

    @EntityGraph(attributePaths = {"category"})
    @Query("""
        select distinct p
        from Product p
        join p.collections c
        where c.slug = :collectionSlug
          and c.status = com.boxfit.backend.domain.enums.CollectionStatus.ACTIVE
          and p.isActive = true
        """)
    Page<Product> findActiveProductsByActiveCollectionSlug(@Param("collectionSlug") String collectionSlug,
                                                           Pageable pageable);
}

