package com.boxfit.backend.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UserRoleUpdateRequest {

    @NotBlank
    @Pattern(regexp = "ADMIN|STAFF|CUSTOMER")
    private String role;

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
