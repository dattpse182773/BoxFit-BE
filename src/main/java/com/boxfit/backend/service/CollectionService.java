package com.boxfit.backend.service;

import com.boxfit.backend.dto.request.collection.CollectionRequest;
import com.boxfit.backend.dto.response.collection.CollectionResponse;
import com.boxfit.backend.dto.response.product.ProductResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CollectionService {
    Page<CollectionResponse> getActiveCollections(Pageable pageable);
    CollectionResponse getActiveBySlug(String slug);
    Page<ProductResponse> getActiveProductsBySlug(String slug, Pageable pageable);
    CollectionResponse create(CollectionRequest request);
    Page<CollectionResponse> getAdminCollections(Pageable pageable);
    CollectionResponse getAdminById(UUID id);
    CollectionResponse update(UUID id, CollectionRequest request);
    CollectionResponse disable(UUID id);
    CollectionResponse enable(UUID id);
}
