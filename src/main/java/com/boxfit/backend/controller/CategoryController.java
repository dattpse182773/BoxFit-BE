package com.boxfit.backend.controller;

import com.boxfit.backend.common.response.ApiResponse;
import com.boxfit.backend.dto.request.category.CategoryRequest;
import com.boxfit.backend.dto.response.category.CategoryResponse;
import com.boxfit.backend.service.CategoryService;
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
@Tag(name = "Category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    @Operation(summary = "Lấy danh sách danh mục đang hiển thị")
    public ResponseEntity<ApiResponse<Page<CategoryResponse>>> getActiveCategories(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", categoryService.getActiveCategories(pageable)));
    }

    @PostMapping("/admin/categories")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Tạo danh mục")
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", categoryService.create(request)));
    }

    @GetMapping("/admin/categories")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Lấy danh sách danh mục cho quản trị")
    public ResponseEntity<ApiResponse<Page<CategoryResponse>>> getAdminCategories(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", categoryService.getAdminCategories(pageable)));
    }

    @GetMapping("/admin/categories/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Lấy chi tiết danh mục cho quản trị")
    public ResponseEntity<ApiResponse<CategoryResponse>> getAdminCategory(@PathVariable UUID id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", categoryService.getAdminById(id)));
    }

    @PutMapping("/admin/categories/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Cập nhật danh mục")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(@PathVariable UUID id, @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", categoryService.update(id, request)));
    }

    @PatchMapping("/admin/categories/{id}/disable")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Ẩn danh mục")
    public ResponseEntity<ApiResponse<CategoryResponse>> disable(@PathVariable UUID id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", categoryService.disable(id)));
    }

    @PatchMapping("/admin/categories/{id}/enable")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Hiển thị lại danh mục")
    public ResponseEntity<ApiResponse<CategoryResponse>> enable(@PathVariable UUID id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", categoryService.enable(id)));
    }
}

