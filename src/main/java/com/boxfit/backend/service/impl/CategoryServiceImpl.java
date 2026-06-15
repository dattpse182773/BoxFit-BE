package com.boxfit.backend.service.impl;

import com.boxfit.backend.common.exception.AppException;
import com.boxfit.backend.common.exception.ErrorCode;
import com.boxfit.backend.common.util.SlugUtils;
import com.boxfit.backend.domain.entity.Category;
import com.boxfit.backend.dto.request.category.CategoryRequest;
import com.boxfit.backend.dto.response.category.CategoryResponse;
import com.boxfit.backend.mapper.CategoryMapper;
import com.boxfit.backend.repository.CategoryRepository;
import com.boxfit.backend.service.CategoryService;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getActiveCategories(Pageable pageable) {
        return categoryRepository.findByIsActiveTrue(pageable).map(categoryMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getAdminCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(categoryMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getAdminById(UUID id) {
        return categoryMapper.toResponse(findByIdOrThrow(id));
    }

    @Override
    public CategoryResponse create(CategoryRequest request) {
        String slug = buildSlug(request.getName());
        if (categoryRepository.existsBySlug(slug)) {
            throw new AppException(ErrorCode.SLUG_ALREADY_EXISTS, "Category slug already exists");
        }

        Category category = new Category();
        category.setSlug(slug);
        categoryMapper.apply(request, category);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse update(UUID id, CategoryRequest request) {
        Category category = findByIdOrThrow(id);
        String slug = buildSlug(request.getName());
        if (!slug.equals(category.getSlug()) && categoryRepository.existsBySlugAndIdNot(slug, id)) {
            throw new AppException(ErrorCode.SLUG_ALREADY_EXISTS, "Category slug already exists");
        }

        category.setSlug(slug);
        categoryMapper.apply(request, category);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse disable(UUID id) {
        Category category = findByIdOrThrow(id);
        category.setActive(false);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse enable(UUID id) {
        Category category = findByIdOrThrow(id);
        category.setActive(true);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    private Category findByIdOrThrow(UUID id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND, "Category not found"));
    }

    private String buildSlug(String name) {
        String slug = SlugUtils.slugify(name);
        if (slug == null) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Invalid category name");
        }
        return slug;
    }
}

