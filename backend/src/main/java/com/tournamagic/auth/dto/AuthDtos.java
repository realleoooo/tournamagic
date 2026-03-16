package com.tournamagic.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {
    public record RegisterRequest(
            @NotBlank String name,
            @NotBlank @Email String email,
            @NotBlank @Size(min = 8) String password
    ) {
    }

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {
    }

    public record SocialLoginRequest(
            @NotBlank String provider,
            @NotBlank String idToken
    ) {
    }

    public record AuthUserDto(String name, String email) {
    }

    public record AuthResponse(String token, AuthUserDto user, boolean created) {
    }

    public record ErrorResponse(String message) {
    }
}
