package com.boxfit.backend.service.impl;

import com.boxfit.backend.common.exception.AppException;
import com.boxfit.backend.common.exception.ErrorCode;
import com.boxfit.backend.domain.entity.User;
import com.boxfit.backend.domain.enums.Role;
import com.boxfit.backend.dto.request.LoginRequest;
import com.boxfit.backend.dto.request.RegisterRequest;
import com.boxfit.backend.dto.response.AuthResponse;
import com.boxfit.backend.dto.response.UserResponse;
import com.boxfit.backend.repository.UserRepository;
import com.boxfit.backend.security.JwtService;
import com.boxfit.backend.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS, "Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(Role.CUSTOMER);
        user.setActive(true);

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Optional<User> opt = userRepository.findByEmail(request.getEmail().toLowerCase().trim());
        if (opt.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_EMAIL_OR_PASSWORD, "Invalid email or password");
        }
        User user = opt.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AppException(ErrorCode.INVALID_EMAIL_OR_PASSWORD, "Invalid email or password");
        }

        if (!user.isActive()) {
            throw new AppException(ErrorCode.USER_INACTIVE, "User is inactive");
        }

        String token = jwtService.generateToken(user);
        AuthResponse resp = new AuthResponse();
        resp.setUser(toResponse(user));
        resp.setAccessToken(token);
        return resp;
    }

    private UserResponse toResponse(User user) {
        UserResponse r = new UserResponse();
        r.setId(user.getId());
        r.setEmail(user.getEmail());
        r.setFullName(user.getFullName());
        r.setPhoneNumber(user.getPhoneNumber());
        r.setRole(user.getRole());
        r.setActive(user.isActive());
        r.setCreatedAt(user.getCreatedAt());
        r.setUpdatedAt(user.getUpdatedAt());
        return r;
    }
}
