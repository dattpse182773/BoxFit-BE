package com.boxfit.backend.controller;

import com.boxfit.backend.common.response.ApiResponse;
import com.boxfit.backend.dto.request.order.CheckoutRequest;
import com.boxfit.backend.dto.request.order.OrderStatusUpdateRequest;
import com.boxfit.backend.dto.request.order.OrderTrackingUpdateRequest;
import com.boxfit.backend.dto.request.order.StaffOrderFilterRequest;
import com.boxfit.backend.dto.response.order.OrderResponse;
import com.boxfit.backend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Tạo đơn hàng từ giỏ hàng hiện tại")
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(@Valid @RequestBody CheckoutRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", orderService.checkout(request)));
    }

    @GetMapping("/orders/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Lấy lịch sử đơn hàng của tôi")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMyOrders(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", orderService.getMyOrders(pageable)));
    }

    @GetMapping("/orders/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Lấy chi tiết đơn hàng của tôi")
    public ResponseEntity<ApiResponse<OrderResponse>> getMyOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", orderService.getMyOrder(orderId)));
    }

    @PostMapping("/orders/{orderId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Hủy đơn hàng khi còn được phép")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelMyOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", orderService.cancelMyOrder(orderId)));
    }

    @GetMapping("/staff/orders")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Lấy danh sách đơn hàng cho nhân viên")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getStaffOrders(@ParameterObject StaffOrderFilterRequest filter,
                                                                           @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", orderService.getStaffOrders(filter, pageable)));
    }

    @GetMapping("/staff/orders/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Lấy chi tiết đơn hàng cho nhân viên")
    public ResponseEntity<ApiResponse<OrderResponse>> getStaffOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", orderService.getStaffOrder(orderId)));
    }

    @PatchMapping("/staff/orders/{orderId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Cập nhật trạng thái đơn hàng")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(@PathVariable UUID orderId,
                                                                   @Valid @RequestBody OrderStatusUpdateRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", orderService.updateStatus(orderId, request)));
    }

    @PatchMapping("/staff/orders/{orderId}/tracking")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Cập nhật mã vận đơn")
    public ResponseEntity<ApiResponse<OrderResponse>> updateTracking(@PathVariable UUID orderId,
                                                                     @Valid @RequestBody OrderTrackingUpdateRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", orderService.updateTracking(orderId, request)));
    }
}
