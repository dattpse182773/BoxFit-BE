package com.boxfit.backend.service;

import com.boxfit.backend.dto.request.user.PasswordUpdateRequest;
import com.boxfit.backend.dto.request.user.ProfileUpdateRequest;
import com.boxfit.backend.dto.response.UserResponse;

public interface UserProfileService {
    UserResponse updateProfile(ProfileUpdateRequest request);
    void updatePassword(PasswordUpdateRequest request);
}
