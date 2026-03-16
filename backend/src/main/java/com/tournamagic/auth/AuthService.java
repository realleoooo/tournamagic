package com.tournamagic.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.tournamagic.dto.auth.AuthRequest;
import com.tournamagic.dto.auth.AuthResponse;
import com.tournamagic.supabase.SupabaseClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
public class AuthService {
    private final SupabaseClient supabaseClient;

    public AuthService(SupabaseClient supabaseClient) {
        this.supabaseClient = supabaseClient;
    }

    public AuthResponse register(AuthRequest request) {
        JsonNode json = supabaseClient.authPost("/auth/v1/signup", Map.of(
                "email", request.email(),
                "password", request.password(),
                "data", Map.of("name", request.name() == null ? "" : request.name().trim())
        ), false);
        return mapAuthResponse(json);
    }

    public AuthResponse login(AuthRequest request) {
        JsonNode json = supabaseClient.authPost("/auth/v1/token", Map.of(
                "email", request.email(),
                "password", request.password()
        ), true);
        return mapAuthResponse(json);
    }

    public String requireUserId(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing bearer token");
        }
        JsonNode user = supabaseClient.authGetUser(accessToken);
        JsonNode id = user.get("id");
        if (id == null || id.isNull()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid session");
        }
        return id.asText();
    }

    private AuthResponse mapAuthResponse(JsonNode json) {
        JsonNode user = json.get("user");
        if (user == null || user.isNull()) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Supabase auth response missing user");
        }
        String userId = user.get("id").asText();
        String email = user.get("email").asText();
        String name = user.path("user_metadata").path("name").asText("");
        return new AuthResponse(
                json.path("access_token").asText(""),
                json.path("refresh_token").asText(""),
                userId,
                email,
                name.isBlank() ? email : name
        );
    }
}
