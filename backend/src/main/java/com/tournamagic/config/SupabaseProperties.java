package com.tournamagic.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "supabase")
public record SupabaseProperties(
        String restUrl,
        String authUrl,
        String anonKey,
        String serviceRoleKey
) {
}
