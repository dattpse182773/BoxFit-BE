package com.boxfit.backend.controller;

import com.boxfit.backend.common.response.ApiResponse;
import com.boxfit.backend.dto.request.LoginRequest;
import com.boxfit.backend.dto.request.RegisterRequest;
import com.boxfit.backend.dto.response.AuthResponse;
import com.boxfit.backend.dto.response.LogoutResponse;
import com.boxfit.backend.dto.response.UserResponse;
import com.boxfit.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.security.core.context.SecurityContextHolder;
import com.boxfit.backend.domain.entity.User;
import com.boxfit.backend.common.exception.AppException;
import com.boxfit.backend.common.exception.ErrorCode;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth")
public class AuthController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản khách hàng")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse user = authService.register(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", user));
    }

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập và nhận JWT")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse auth = authService.login(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", auth));
    }

    @PostMapping("/logout")
    @Operation(summary = "Đăng xuất và vô hiệu hóa token hiện tại")
    public ResponseEntity<LogoutResponse> logout(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        String token = extractBearerToken(authorizationHeader);
        LogoutResponse response = authService.logout(token);
        return ResponseEntity.ok(response);
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null) {
            return null;
        }
        if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return authorizationHeader.substring(BEARER_PREFIX.length()).trim();
    }

    @GetMapping("/me")
    @Operation(summary = "Lấy thông tin người dùng đang đăng nhập")
    public ResponseEntity<ApiResponse<UserResponse>> me() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new AppException(ErrorCode.MISSING_TOKEN, "Not authenticated");
        }

        if (!(auth.getPrincipal() instanceof User user)) {
            throw new AppException(ErrorCode.INVALID_TOKEN, "Invalid authentication principal");
        }

        UserResponse resp = new UserResponse();
        resp.setId(user.getId());
        resp.setEmail(user.getEmail());
        resp.setFullName(user.getFullName());
        resp.setPhoneNumber(user.getPhoneNumber());
        resp.setRole(user.getRole().getName());
        resp.setActive(user.isActive());
        resp.setCreatedAt(user.getCreatedAt());
        resp.setUpdatedAt(user.getUpdatedAt());

        return ResponseEntity.ok(new ApiResponse<>(true, "Success", resp));
    }
}
