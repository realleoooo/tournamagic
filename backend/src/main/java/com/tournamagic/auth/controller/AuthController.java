package com.tournamagic.auth.controller;

import com.tournamagic.auth.dto.AuthDtos;
import com.tournamagic.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthDtos.AuthResponse register(@Valid @RequestBody AuthDtos.RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthDtos.AuthResponse login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/social")
    public AuthDtos.AuthResponse socialLogin(@Valid @RequestBody AuthDtos.SocialLoginRequest request) {
        return authService.socialLogin(request);
    }

    @GetMapping("/session")
    public AuthDtos.AuthUserDto session(@RequestHeader("Authorization") String authorization) {
        String token = extractBearerToken(authorization);
        var user = authService.requireUserByToken(token);
        return new AuthDtos.AuthUserDto(user.getName(), user.getEmail());
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader("Authorization") String authorization) {
        authService.logout(extractBearerToken(authorization));
    }

    private String extractBearerToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new com.tournamagic.auth.service.AuthException("Unauthorized");
        }
        return authorization.substring("Bearer ".length()).trim();
    }
}
