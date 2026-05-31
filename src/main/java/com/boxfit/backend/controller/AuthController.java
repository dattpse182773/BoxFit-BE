package com.boxfit.backend.controller;

import com.boxfit.backend.common.response.ApiResponse;
import com.boxfit.backend.dto.request.LoginRequest;
import com.boxfit.backend.dto.request.RegisterRequest;
import com.boxfit.backend.dto.response.AuthResponse;
import com.boxfit.backend.dto.response.UserResponse;
import com.boxfit.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse user = authService.register(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", user));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse auth = authService.login(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", auth));
    }
}
