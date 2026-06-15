package com.boxfit.backend.service;

import com.boxfit.backend.dto.request.order.CheckoutRequest;
import com.boxfit.backend.dto.request.order.OrderStatusUpdateRequest;
import com.boxfit.backend.dto.request.order.OrderTrackingUpdateRequest;
import com.boxfit.backend.dto.request.order.StaffOrderFilterRequest;
import com.boxfit.backend.dto.response.order.OrderResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse checkout(CheckoutRequest request);
    Page<OrderResponse> getMyOrders(Pageable pageable);
    OrderResponse getMyOrder(UUID orderId);
    OrderResponse cancelMyOrder(UUID orderId);
    Page<OrderResponse> getStaffOrders(StaffOrderFilterRequest filter, Pageable pageable);
    OrderResponse getStaffOrder(UUID orderId);
    OrderResponse updateStatus(UUID orderId, OrderStatusUpdateRequest request);
    OrderResponse updateTracking(UUID orderId, OrderTrackingUpdateRequest request);
}
