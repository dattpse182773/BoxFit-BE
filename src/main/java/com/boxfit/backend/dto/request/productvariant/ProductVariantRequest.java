package com.boxfit.backend.dto.request.productvariant;

import com.boxfit.backend.domain.enums.ProductVariantSize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.UUID;

public class ProductVariantRequest {

    @NotNull
    private UUID productId;

    @NotNull
    private ProductVariantSize size;

    @NotBlank
    private String color;

    @NotBlank
    private String sku;

    @NotNull
    @PositiveOrZero
    private Integer stockQuantity;

    private BigDecimal priceAdjustment;

    private Boolean isActive;

    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }

    public ProductVariantSize getSize() { return size; }
    public void setSize(ProductVariantSize size) { this.size = size; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public BigDecimal getPriceAdjustment() { return priceAdjustment; }
    public void setPriceAdjustment(BigDecimal priceAdjustment) { this.priceAdjustment = priceAdjustment; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }
}

