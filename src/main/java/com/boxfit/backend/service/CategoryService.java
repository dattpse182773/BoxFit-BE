package com.boxfit.backend.service;

import com.boxfit.backend.dto.request.category.CategoryRequest;
import com.boxfit.backend.dto.response.category.CategoryResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    Page<CategoryResponse> getActiveCategories(Pageable pageable);
    Page<CategoryResponse> getAdminCategories(Pageable pageable);
    CategoryResponse getAdminById(UUID id);
    CategoryResponse create(CategoryRequest request);
    CategoryResponse update(UUID id, CategoryRequest request);
    CategoryResponse disable(UUID id);
    CategoryResponse enable(UUID id);
}

