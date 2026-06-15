package com.boxfit.backend.dto.response;

public record LogoutResponse(
    boolean success,
    String message
) {
}

