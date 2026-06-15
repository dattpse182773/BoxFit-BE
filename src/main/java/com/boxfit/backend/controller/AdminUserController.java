package com.boxfit.backend.controller;

import com.boxfit.backend.common.response.ApiResponse;
import com.boxfit.backend.dto.request.user.CreateStaffRequest;
import com.boxfit.backend.dto.request.user.UserRoleUpdateRequest;
import com.boxfit.backend.dto.response.UserResponse;
import com.boxfit.backend.service.AdminUserService;
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
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin User")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping("/users")
    @Operation(summary = "Lấy danh sách người dùng")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsers(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", adminUserService.getUsers(pageable)));
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Lấy chi tiết người dùng")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable UUID id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", adminUserService.getUser(id)));
    }

    @PatchMapping("/users/{id}/role")
    @Operation(summary = "Cập nhật vai trò người dùng")
    public ResponseEntity<ApiResponse<UserResponse>> updateRole(@PathVariable UUID id,
                                                                @Valid @RequestBody UserRoleUpdateRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", adminUserService.updateRole(id, request)));
    }

    @PatchMapping("/users/{id}/disable")
    @Operation(summary = "Vô hiệu hóa người dùng")
    public ResponseEntity<ApiResponse<UserResponse>> disableUser(@PathVariable UUID id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", adminUserService.disable(id)));
    }

    @PatchMapping("/users/{id}/enable")
    @Operation(summary = "Kích hoạt lại người dùng")
    public ResponseEntity<ApiResponse<UserResponse>> enableUser(@PathVariable UUID id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", adminUserService.enable(id)));
    }

    @PostMapping("/staff")
    @Operation(summary = "Tạo tài khoản nhân viên")
    public ResponseEntity<ApiResponse<UserResponse>> createStaff(@Valid @RequestBody CreateStaffRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", adminUserService.createStaff(request)));
    }

    @GetMapping("/staff")
    @Operation(summary = "Lấy danh sách nhân viên")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getStaff(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", adminUserService.getStaff(pageable)));
    }
}
