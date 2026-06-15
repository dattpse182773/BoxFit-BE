package com.boxfit.backend.repository.specification;

import com.boxfit.backend.domain.entity.Product;
import com.boxfit.backend.domain.enums.ProductStatus;
import com.boxfit.backend.domain.enums.ProductVariantSize;
import java.math.BigDecimal;
import org.springframework.data.jpa.domain.Specification;

public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<Product> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("isActive"));
    }

    public static Specification<Product> categorySlugEquals(String categorySlug) {
        return (root, query, cb) -> {
            if (categorySlug == null || categorySlug.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(root.join("category").get("slug"), categorySlug);
        };
    }

    public static Specification<Product> hasVariantSize(ProductVariantSize size) {
        return (root, query, cb) -> {
            if (size == null) {
                return cb.conjunction();
            }
            query.distinct(true);
            var variantJoin = root.join("variants", jakarta.persistence.criteria.JoinType.INNER);
            return cb.equal(variantJoin.get("size"), size);
        };
    }

    public static Specification<Product> hasVariantColor(String color) {
        return (root, query, cb) -> {
            if (color == null || color.isBlank()) {
                return cb.conjunction();
            }
            query.distinct(true);
            var variantJoin = root.join("variants", jakarta.persistence.criteria.JoinType.INNER);
            return cb.equal(cb.lower(variantJoin.get("color")), color.toLowerCase());
        };
    }

    public static Specification<Product> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            var salePricePath = root.<BigDecimal>get("salePrice");
            var pricePath = root.<BigDecimal>get("price");
            var salePriceNotNull = cb.isNotNull(salePricePath);

            if (minPrice != null && maxPrice != null) {
                return cb.or(
                    cb.and(salePriceNotNull, cb.between(salePricePath, minPrice, maxPrice)),
                    cb.and(cb.isNull(salePricePath), cb.between(pricePath, minPrice, maxPrice))
                );
            }
            if (minPrice != null) {
                return cb.or(
                    cb.and(salePriceNotNull, cb.greaterThanOrEqualTo(salePricePath, minPrice)),
                    cb.and(cb.isNull(salePricePath), cb.greaterThanOrEqualTo(pricePath, minPrice))
                );
            }
            if (maxPrice != null) {
                return cb.or(
                    cb.and(salePriceNotNull, cb.lessThanOrEqualTo(salePricePath, maxPrice)),
                    cb.and(cb.isNull(salePricePath), cb.lessThanOrEqualTo(pricePath, maxPrice))
                );
            }
            return cb.conjunction();
        };
    }

    public static Specification<Product> genderEquals(String gender) {
        return (root, query, cb) -> {
            if (gender == null || gender.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(cb.lower(root.get("gender")), gender.toLowerCase());
        };
    }

    public static Specification<Product> sportTypeEquals(String sportType) {
        return (root, query, cb) -> {
            if (sportType == null || sportType.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(cb.lower(root.get("sportType")), sportType.toLowerCase());
        };
    }

    public static Specification<Product> statusEquals(ProductStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("status"), status);
        };
    }
}


