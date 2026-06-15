package com.boxfit.backend.mapper;

import com.boxfit.backend.domain.entity.ProductVariant;
import com.boxfit.backend.dto.request.productvariant.ProductVariantRequest;
import com.boxfit.backend.dto.response.productvariant.ProductVariantResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductVariantMapper {

    public ProductVariantResponse toResponse(ProductVariant variant) {
        if (variant == null) {
            return null;
        }

        ProductVariantResponse response = new ProductVariantResponse();
        response.setId(variant.getId());
        response.setProductId(variant.getProduct() == null ? null : variant.getProduct().getId());
        response.setProductSlug(variant.getProduct() == null ? null : variant.getProduct().getSlug());
        response.setSize(variant.getSize());
        response.setColor(variant.getColor());
        response.setSku(variant.getSku());
        response.setStockQuantity(variant.getStockQuantity());
        response.setPriceAdjustment(variant.getPriceAdjustment());
        response.setActive(variant.isActive());
        response.setCreatedAt(variant.getCreatedAt());
        response.setUpdatedAt(variant.getUpdatedAt());
        return response;
    }

    public void apply(ProductVariantRequest request, ProductVariant variant) {
        variant.setSize(request.getSize());
        variant.setColor(request.getColor());
        variant.setSku(request.getSku());
        variant.setStockQuantity(request.getStockQuantity());
        variant.setPriceAdjustment(request.getPriceAdjustment());
        if (request.getIsActive() != null) {
            variant.setActive(request.getIsActive());
        }
    }
}

