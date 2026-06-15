package com.boxfit.backend.mapper;

import com.boxfit.backend.domain.entity.Product;
import com.boxfit.backend.domain.entity.Category;
import com.boxfit.backend.dto.request.product.ProductRequest;
import com.boxfit.backend.dto.response.product.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    private final CategoryMapper categoryMapper;

    public ProductMapper(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }

        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setSlug(product.getSlug());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setSalePrice(product.getSalePrice());
        response.setGender(product.getGender());
        response.setSportType(product.getSportType());
        response.setStatus(product.getStatus());
        response.setCategory(categoryMapper.toResponse(product.getCategory()));
        response.setImageUrl(product.getImageUrl());
        response.setActive(product.isActive());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }

    public void apply(ProductRequest request, Product product, Category category) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setSalePrice(request.getSalePrice());
        product.setGender(request.getGender());
        product.setSportType(request.getSportType());
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }
        product.setCategory(category);
        product.setImageUrl(request.getImageUrl());
        if (request.getIsActive() != null) {
            product.setActive(request.getIsActive());
        }
    }

}

