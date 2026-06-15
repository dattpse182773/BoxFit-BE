package com.boxfit.backend.dto.request.productvariant;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class ProductVariantStockRequest {

    @NotNull
    @PositiveOrZero
    private Integer stockQuantity;

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
}
