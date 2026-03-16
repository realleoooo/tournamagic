package com.tournamagic.supabase;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tournamagic.config.SupabaseProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

@Component
public class SupabaseClient {
    private final SupabaseProperties properties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public SupabaseClient(SupabaseProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    }

    public JsonNode authPost(String path, Object payload, boolean passwordGrant) {
        String query = passwordGrant ? "?grant_type=password" : "";
        HttpRequest request = request(properties.authUrl() + path + query)
                .header("apikey", properties.anonKey())
                .header("Authorization", "Bearer " + properties.anonKey())
                .POST(jsonBody(payload))
                .build();
        return sendJson(request);
    }

    public JsonNode authGetUser(String accessToken) {
        HttpRequest request = request(properties.authUrl() + "/auth/v1/user")
                .header("apikey", properties.anonKey())
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();
        return sendJson(request);
    }

    public JsonNode select(String table, Map<String, String> filters, String select) {
        StringBuilder query = new StringBuilder("select=").append(encode(select));
        filters.forEach((k, v) -> query.append("&").append(encode(k)).append("=").append(encode(v)));
        HttpRequest request = request(properties.restUrl() + "/" + table + "?" + query)
                .header("apikey", properties.serviceRoleKey())
                .header("Authorization", "Bearer " + properties.serviceRoleKey())
                .GET()
                .build();
        return sendJson(request);
    }

    public JsonNode insert(String table, Object payload) {
        HttpRequest request = request(properties.restUrl() + "/" + table)
                .header("apikey", properties.serviceRoleKey())
                .header("Authorization", "Bearer " + properties.serviceRoleKey())
                .header("Prefer", "return=representation")
                .POST(jsonBody(payload))
                .build();
        return sendJson(request);
    }

    public JsonNode update(String table, Map<String, String> filters, Object payload) {
        StringBuilder query = new StringBuilder();
        filters.forEach((k, v) -> {
            if (!query.isEmpty()) query.append("&");
            query.append(encode(k)).append("=").append(encode(v));
        });
        HttpRequest request = request(properties.restUrl() + "/" + table + "?" + query)
                .header("apikey", properties.serviceRoleKey())
                .header("Authorization", "Bearer " + properties.serviceRoleKey())
                .header("Prefer", "return=representation")
                .method("PATCH", jsonBody(payload))
                .build();
        return sendJson(request);
    }

    public void delete(String table, Map<String, String> filters) {
        StringBuilder query = new StringBuilder();
        filters.forEach((k, v) -> {
            if (!query.isEmpty()) query.append("&");
            query.append(encode(k)).append("=").append(encode(v));
        });
        HttpRequest request = request(properties.restUrl() + "/" + table + "?" + query)
                .header("apikey", properties.serviceRoleKey())
                .header("Authorization", "Bearer " + properties.serviceRoleKey())
                .DELETE()
                .build();
        sendJson(request);
    }

    private HttpRequest.Builder request(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/json");
    }

    private HttpRequest.BodyPublisher jsonBody(Object payload) {
        try {
            return HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload));
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to encode request", ex);
        }
    }

    private JsonNode sendJson(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body() == null ? "" : response.body();
            if (response.statusCode() >= 400) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Supabase request failed: " + body);
            }
            if (body.isBlank()) {
                return objectMapper.createArrayNode();
            }
            return objectMapper.readTree(body);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Supabase is unavailable", ex);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Supabase is unavailable", ex);
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
