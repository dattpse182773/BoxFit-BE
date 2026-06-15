package com.boxfit.backend.dto.request.order;

import com.boxfit.backend.domain.enums.OrderStatus;
import java.time.Instant;

public class StaffOrderFilterRequest {

    private OrderStatus status;
    private Instant createdFrom;
    private Instant createdTo;
    private String keyword;

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public Instant getCreatedFrom() { return createdFrom; }
    public void setCreatedFrom(Instant createdFrom) { this.createdFrom = createdFrom; }

    public Instant getCreatedTo() { return createdTo; }
    public void setCreatedTo(Instant createdTo) { this.createdTo = createdTo; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
}
