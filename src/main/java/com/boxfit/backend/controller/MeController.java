package com.boxfit.backend.controller;

import com.boxfit.backend.common.response.ApiResponse;
import com.boxfit.backend.dto.request.user.PasswordUpdateRequest;
import com.boxfit.backend.dto.request.user.ProfileUpdateRequest;
import com.boxfit.backend.dto.response.UserResponse;
import com.boxfit.backend.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
@Tag(name = "Me")
public class MeController {

    private final UserProfileService userProfileService;

    public MeController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PutMapping("/profile")
    @Operation(summary = "Cập nhật thông tin cá nhân")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", userProfileService.updateProfile(request)));
    }

    @PutMapping("/password")
    @Operation(summary = "Đổi mật khẩu tài khoản")
    public ResponseEntity<ApiResponse<Object>> updatePassword(@Valid @RequestBody PasswordUpdateRequest request) {
        userProfileService.updatePassword(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", null));
    }
}
