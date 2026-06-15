package com.boxfit.backend.service.impl;

import com.boxfit.backend.common.exception.AppException;
import com.boxfit.backend.common.exception.ErrorCode;
import com.boxfit.backend.domain.entity.Role;
import com.boxfit.backend.domain.entity.User;
import com.boxfit.backend.dto.request.user.CreateStaffRequest;
import com.boxfit.backend.dto.request.user.UserRoleUpdateRequest;
import com.boxfit.backend.dto.response.UserResponse;
import com.boxfit.backend.repository.RoleRepository;
import com.boxfit.backend.repository.UserRepository;
import com.boxfit.backend.service.AdminUserService;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserServiceImpl(UserRepository userRepository,
                                RoleRepository roleRepository,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUser(UUID userId) {
        return toResponse(findUser(userId));
    }

    @Override
    public UserResponse updateRole(UUID userId, UserRoleUpdateRequest request) {
        User user = findUser(userId);
        user.setRole(findRole(request.getRole()));
        return toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse disable(UUID userId) {
        User user = findUser(userId);
        user.setActive(false);
        return toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse enable(UUID userId) {
        User user = findUser(userId);
        user.setActive(true);
        return toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse createStaff(CreateStaffRequest request) {
        String email = request.getEmail().toLowerCase().trim();
        if (userRepository.existsByEmail(email)) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS, "Email already exists");
        }

        User staff = new User();
        staff.setEmail(email);
        staff.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        staff.setFullName(request.getFullName().trim());
        staff.setPhoneNumber(request.getPhoneNumber() == null ? null : request.getPhoneNumber().trim());
        staff.setRole(findRole(Role.STAFF));
        staff.setActive(true);
        return toResponse(userRepository.save(staff));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getStaff(Pageable pageable) {
        return userRepository.findByRoleName(Role.STAFF, pageable).map(this::toResponse);
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "User not found"));
    }

    private Role findRole(String roleName) {
        return roleRepository.findByName(roleName)
            .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, "Role not found"));
    }

    private UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setRole(user.getRole().getName());
        response.setActive(user.isActive());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}
