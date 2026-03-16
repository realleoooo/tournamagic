package com.tournamagic.dto.auth;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String userId,
        String email,
        String name
) {
}
