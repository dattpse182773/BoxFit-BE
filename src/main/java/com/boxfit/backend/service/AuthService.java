package com.boxfit.backend.service;

import com.boxfit.backend.dto.request.LoginRequest;
import com.boxfit.backend.dto.request.RegisterRequest;
import com.boxfit.backend.dto.response.AuthResponse;
import com.boxfit.backend.dto.response.UserResponse;

public interface AuthService {
    UserResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
