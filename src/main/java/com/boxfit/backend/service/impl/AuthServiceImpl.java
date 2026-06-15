package com.boxfit.backend.service.impl;

import com.boxfit.backend.common.exception.AppException;
import com.boxfit.backend.common.exception.ErrorCode;
import com.boxfit.backend.domain.entity.Role;
import com.boxfit.backend.domain.entity.User;
import com.boxfit.backend.dto.request.LoginRequest;
import com.boxfit.backend.dto.request.RegisterRequest;
import com.boxfit.backend.dto.response.AuthResponse;
import com.boxfit.backend.dto.response.LogoutResponse;
import com.boxfit.backend.dto.response.UserResponse;
import com.boxfit.backend.repository.RoleRepository;
import com.boxfit.backend.repository.UserRepository;
import com.boxfit.backend.security.JwtService;
import com.boxfit.backend.service.AuthService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
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
        user.setRole(roleRepository.findByName(Role.CUSTOMER)
            .orElseThrow(() -> new AppException(ErrorCode.INTERNAL_ERROR, "Default role not found")));
        user.setActive(true);

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().toLowerCase().trim();

        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );
        } catch (DisabledException ex) {
            throw new AppException(ErrorCode.USER_INACTIVE, "User is inactive");
        } catch (AuthenticationException ex) {
            throw new AppException(ErrorCode.INVALID_EMAIL_OR_PASSWORD, "Invalid email or password");
        }

        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_EMAIL_OR_PASSWORD, "Invalid email or password");
        }
        User user = opt.get();

        if (!user.isActive()) {
            throw new AppException(ErrorCode.USER_INACTIVE, "User is inactive");
        }

        String token = jwtService.generateToken(user);
        AuthResponse resp = new AuthResponse();
        resp.setUser(toResponse(user));
        resp.setAccessToken(token);
        return resp;
    }

    @Override
    public LogoutResponse logout(String token) {
        if (token == null || token.isBlank()) {
            throw new AppException(ErrorCode.MISSING_TOKEN, "Missing token");
        }

        try {
            jwtService.validateToken(token);
            Instant expiredAt = jwtService.extractExpiration(token);
            jwtService.blacklistToken(token, expiredAt);
        } catch (ExpiredJwtException ex) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED, "Expired token");
        } catch (IllegalArgumentException | JwtException ex) {
            throw new AppException(ErrorCode.INVALID_TOKEN, "Invalid token");
        } finally {
            SecurityContextHolder.clearContext();
        }

        return new LogoutResponse(true, "Logged out successfully");
    }

    private UserResponse toResponse(User user) {
        UserResponse r = new UserResponse();
        r.setId(user.getId());
        r.setEmail(user.getEmail());
        r.setFullName(user.getFullName());
        r.setPhoneNumber(user.getPhoneNumber());
        r.setRole(user.getRole().getName());
        r.setActive(user.isActive());
        r.setCreatedAt(user.getCreatedAt());
        r.setUpdatedAt(user.getUpdatedAt());
        return r;
    }
}
