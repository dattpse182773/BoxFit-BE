package com.boxfit.backend.controller;

import com.boxfit.backend.common.response.ApiResponse;
import com.boxfit.backend.dto.request.productvariant.ProductVariantRequest;
import com.boxfit.backend.dto.request.productvariant.ProductVariantStockRequest;
import com.boxfit.backend.dto.response.productvariant.ProductVariantResponse;
import com.boxfit.backend.service.ProductVariantService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/product-variants")
@Tag(name = "Product Variant")
public class ProductVariantController {

    private final ProductVariantService productVariantService;

    public ProductVariantController(ProductVariantService productVariantService) {
        this.productVariantService = productVariantService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Lấy danh sách biến thể sản phẩm cho quản trị")
    public ResponseEntity<ApiResponse<Page<ProductVariantResponse>>> getAdminVariants(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", productVariantService.getAdminVariants(pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Lấy chi tiết biến thể sản phẩm")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> getAdminVariant(@PathVariable UUID id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", productVariantService.getAdminById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Tạo biến thể sản phẩm")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> create(@Valid @RequestBody ProductVariantRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", productVariantService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Cập nhật biến thể sản phẩm")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> update(@PathVariable UUID id,
                                                                      @Valid @RequestBody ProductVariantRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", productVariantService.update(id, request)));
    }

    @PatchMapping("/{id}/disable")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Ẩn biến thể sản phẩm")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> disable(@PathVariable UUID id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", productVariantService.disable(id)));
    }

    @PatchMapping("/{id}/enable")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Hiển thị lại biến thể sản phẩm")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> enable(@PathVariable UUID id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", productVariantService.enable(id)));
    }

    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Cập nhật tồn kho biến thể sản phẩm")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> updateStock(@PathVariable UUID id,
                                                                           @Valid @RequestBody ProductVariantStockRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", productVariantService.updateStock(id, request)));
    }
}

