package com.boxfit.backend.service;

import com.boxfit.backend.dto.request.productvariant.ProductVariantRequest;
import com.boxfit.backend.dto.request.productvariant.ProductVariantStockRequest;
import com.boxfit.backend.dto.response.productvariant.ProductVariantResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductVariantService {
    Page<ProductVariantResponse> getByProductId(UUID productId, Pageable pageable);
    Page<ProductVariantResponse> getByProductSlug(String productSlug, Pageable pageable);
    Page<ProductVariantResponse> getAdminVariants(Pageable pageable);
    ProductVariantResponse getAdminById(UUID id);
    ProductVariantResponse create(ProductVariantRequest request);
    ProductVariantResponse update(UUID id, ProductVariantRequest request);
    ProductVariantResponse disable(UUID id);
    ProductVariantResponse enable(UUID id);
    ProductVariantResponse updateStock(UUID id, ProductVariantStockRequest request);
}

