package com.boxfit.backend.controller;

import com.boxfit.backend.common.response.ApiResponse;
import com.boxfit.backend.dto.request.shipping.ShippingQuoteRequest;
import com.boxfit.backend.dto.response.shipping.ShippingMethodResponse;
import com.boxfit.backend.dto.response.shipping.ShippingQuoteResponse;
import com.boxfit.backend.service.ShippingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shipping")
@Tag(name = "Shipping")
public class ShippingController {

    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @GetMapping("/methods")
    @Operation(summary = "Lấy danh sách phương thức vận chuyển")
    public ResponseEntity<ApiResponse<List<ShippingMethodResponse>>> getMethods() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", shippingService.getMethods()));
    }

    @PostMapping("/quote")
    @Operation(summary = "Tính phí vận chuyển tạm tính")
    public ResponseEntity<ApiResponse<ShippingQuoteResponse>> quote(@Valid @RequestBody ShippingQuoteRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", shippingService.quote(request)));
    }
}
