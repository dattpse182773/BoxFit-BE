package com.boxfit.backend.repository.specification;

import com.boxfit.backend.domain.entity.Order;
import com.boxfit.backend.domain.enums.OrderStatus;
import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;

public final class OrderSpecifications {

    private OrderSpecifications() {
    }

    public static Specification<Order> statusEquals(OrderStatus status) {
        return (root, query, cb) -> status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    public static Specification<Order> createdFrom(Instant createdFrom) {
        return (root, query, cb) -> createdFrom == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("createdAt"), createdFrom);
    }

    public static Specification<Order> createdTo(Instant createdTo) {
        return (root, query, cb) -> createdTo == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("createdAt"), createdTo);
    }

    public static Specification<Order> keywordContains(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }
            String like = "%" + keyword.toLowerCase().trim() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("recipientName")), like),
                cb.like(cb.lower(root.get("phoneNumber")), like),
                cb.like(cb.lower(root.get("trackingCode")), like),
                cb.like(cb.lower(root.join("user").get("email")), like)
            );
        };
    }
}
