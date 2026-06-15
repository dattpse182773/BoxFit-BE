package com.boxfit.backend.service;

import com.boxfit.backend.dto.request.user.CreateStaffRequest;
import com.boxfit.backend.dto.request.user.UserRoleUpdateRequest;
import com.boxfit.backend.dto.response.UserResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminUserService {
    Page<UserResponse> getUsers(Pageable pageable);
    UserResponse getUser(UUID userId);
    UserResponse updateRole(UUID userId, UserRoleUpdateRequest request);
    UserResponse disable(UUID userId);
    UserResponse enable(UUID userId);
    UserResponse createStaff(CreateStaffRequest request);
    Page<UserResponse> getStaff(Pageable pageable);
}
