package com.boxfit.backend.controller;

import com.boxfit.backend.common.response.ApiResponse;
import com.boxfit.backend.dto.request.product.ProductFilterRequest;
import com.boxfit.backend.dto.request.product.ProductRequest;
import com.boxfit.backend.dto.response.product.ProductImageUploadResponse;
import com.boxfit.backend.dto.response.product.ProductResponse;
import com.boxfit.backend.dto.response.productvariant.ProductVariantResponse;
import com.boxfit.backend.service.ProductImageStorageService;
import com.boxfit.backend.service.ProductService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@Tag(name = "Product")
public class ProductController {

    private final ProductService productService;
    private final ProductVariantService productVariantService;
    private final ProductImageStorageService productImageStorageService;

    public ProductController(ProductService productService,
                             ProductVariantService productVariantService,
                             ProductImageStorageService productImageStorageService) {
        this.productService = productService;
        this.productVariantService = productVariantService;
        this.productImageStorageService = productImageStorageService;
    }

    @GetMapping("/products")
    @Operation(summary = "Lấy danh sách sản phẩm kèm bộ lọc")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProducts(@ParameterObject ProductFilterRequest filter,
                                                                          @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", productService.getProducts(filter, pageable)));
    }

    @GetMapping("/products/{slug}")
    @Operation(summary = "Lấy chi tiết sản phẩm theo slug")
    public ResponseEntity<ApiResponse<ProductResponse>> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", productService.getBySlug(slug)));
    }

    @GetMapping("/products/{productId}/variants")
    @Operation(summary = "Lấy danh sách biến thể đang bán theo mã sản phẩm")
    public ResponseEntity<ApiResponse<Page<ProductVariantResponse>>> getVariantsByProductId(@PathVariable UUID productId,
                                                                                            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", productVariantService.getByProductId(productId, pageable)));
    }

    @GetMapping("/products/by-slug/{productSlug}/variants")
    @Operation(summary = "Lấy danh sách biến thể đang bán theo slug sản phẩm")
    public ResponseEntity<ApiResponse<Page<ProductVariantResponse>>> getVariantsByProductSlug(@PathVariable String productSlug,
                                                                                              @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", productVariantService.getByProductSlug(productSlug, pageable)));
    }

    @PostMapping("/admin/products")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Tạo sản phẩm")
    public ResponseEntity<ApiResponse<ProductResponse>> create(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", productService.create(request)));
    }

    @GetMapping("/admin/products")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Lấy danh sách sản phẩm cho quản trị")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAdminProducts(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", productService.getAdminProducts(pageable)));
    }

    @GetMapping("/admin/products/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Lấy chi tiết sản phẩm cho quản trị")
    public ResponseEntity<ApiResponse<ProductResponse>> getAdminProduct(@PathVariable UUID id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", productService.getAdminById(id)));
    }

    @PostMapping(value = "/admin/products/image", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Tải ảnh sản phẩm lên S3")
    public ResponseEntity<ApiResponse<ProductImageUploadResponse>> uploadImage(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", productImageStorageService.uploadProductImage(file)));
    }

    @PutMapping("/admin/products/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Cập nhật sản phẩm")
    public ResponseEntity<ApiResponse<ProductResponse>> update(@PathVariable UUID id, @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", productService.update(id, request)));
    }

    @PatchMapping("/admin/products/{id}/disable")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Ẩn sản phẩm")
    public ResponseEntity<ApiResponse<ProductResponse>> disable(@PathVariable UUID id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", productService.disable(id)));
    }

    @PatchMapping("/admin/products/{id}/enable")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Hiển thị lại sản phẩm")
    public ResponseEntity<ApiResponse<ProductResponse>> enable(@PathVariable UUID id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", productService.enable(id)));
    }
}

