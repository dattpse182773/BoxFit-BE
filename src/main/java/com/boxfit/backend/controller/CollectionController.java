package com.boxfit.backend.controller;

import com.boxfit.backend.common.response.ApiResponse;
import com.boxfit.backend.dto.request.collection.CollectionRequest;
import com.boxfit.backend.dto.response.collection.CollectionResponse;
import com.boxfit.backend.dto.response.product.ProductResponse;
import com.boxfit.backend.service.CollectionService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Collection")
public class CollectionController {

    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping("/collections")
    @Operation(summary = "Lấy danh sách bộ sưu tập đang hiển thị")
    public ResponseEntity<ApiResponse<Page<CollectionResponse>>> getCollections(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", collectionService.getActiveCollections(pageable)));
    }

    @GetMapping("/collections/{slug}")
    @Operation(summary = "Lấy chi tiết bộ sưu tập theo slug")
    public ResponseEntity<ApiResponse<CollectionResponse>> getCollection(@PathVariable String slug) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", collectionService.getActiveBySlug(slug)));
    }

    @GetMapping("/collections/{slug}/products")
    @Operation(summary = "Lấy danh sách sản phẩm trong bộ sưu tập")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getCollectionProducts(@PathVariable String slug,
                                                                                   @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", collectionService.getActiveProductsBySlug(slug, pageable)));
    }

    @PostMapping("/admin/collections")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Tạo bộ sưu tập")
    public ResponseEntity<ApiResponse<CollectionResponse>> create(@Valid @RequestBody CollectionRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", collectionService.create(request)));
    }

    @GetMapping("/admin/collections")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Lấy danh sách bộ sưu tập cho quản trị")
    public ResponseEntity<ApiResponse<Page<CollectionResponse>>> getAdminCollections(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", collectionService.getAdminCollections(pageable)));
    }

    @GetMapping("/admin/collections/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Lấy chi tiết bộ sưu tập cho quản trị")
    public ResponseEntity<ApiResponse<CollectionResponse>> getAdminCollection(@PathVariable UUID id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", collectionService.getAdminById(id)));
    }

    @PutMapping("/admin/collections/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Cập nhật bộ sưu tập")
    public ResponseEntity<ApiResponse<CollectionResponse>> update(@PathVariable UUID id,
                                                                  @Valid @RequestBody CollectionRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", collectionService.update(id, request)));
    }

    @PatchMapping("/admin/collections/{id}/disable")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Ẩn bộ sưu tập")
    public ResponseEntity<ApiResponse<CollectionResponse>> disable(@PathVariable UUID id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", collectionService.disable(id)));
    }

    @PatchMapping("/admin/collections/{id}/enable")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Hiển thị lại bộ sưu tập")
    public ResponseEntity<ApiResponse<CollectionResponse>> enable(@PathVariable UUID id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", collectionService.enable(id)));
    }
}
