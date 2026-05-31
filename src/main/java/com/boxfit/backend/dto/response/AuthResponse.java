package com.boxfit.backend.dto.response;

public class AuthResponse {
    private UserResponse user;
    private String accessToken;

    public UserResponse getUser() { return user; }
    public void setUser(UserResponse user) { this.user = user; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
}
