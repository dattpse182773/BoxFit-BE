package com.boxfit.backend.dto.request.product;

import com.boxfit.backend.domain.enums.ProductStatus;
import com.boxfit.backend.domain.enums.ProductVariantSize;
import java.math.BigDecimal;

public class ProductFilterRequest {

    private String categorySlug;
    private ProductVariantSize variantSize;
    private String color;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String gender;
    private String sportType;
    private ProductStatus status;

    public String getCategorySlug() { return categorySlug; }
    public void setCategorySlug(String categorySlug) { this.categorySlug = categorySlug; }

    public ProductVariantSize getVariantSize() { return variantSize; }
    public void setVariantSize(ProductVariantSize variantSize) { this.variantSize = variantSize; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }

    public BigDecimal getMaxPrice() { return maxPrice; }
    public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getSportType() { return sportType; }
    public void setSportType(String sportType) { this.sportType = sportType; }

    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }
}

