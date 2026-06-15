package com.boxfit.backend.service;

import com.boxfit.backend.dto.request.product.ProductFilterRequest;
import com.boxfit.backend.dto.request.product.ProductRequest;
import com.boxfit.backend.dto.response.product.ProductResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductResponse> getProducts(ProductFilterRequest filter, Pageable pageable);
    ProductResponse getBySlug(String slug);
    Page<ProductResponse> getAdminProducts(Pageable pageable);
    ProductResponse getAdminById(UUID id);
    ProductResponse create(ProductRequest request);
    ProductResponse update(UUID id, ProductRequest request);
    ProductResponse disable(UUID id);
    ProductResponse enable(UUID id);
}

