package com.tournamagic.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "auth")
public class AuthProperties {
    private final Session session = new Session();
    private final Google google = new Google();
    private final Apple apple = new Apple();

    public Session getSession() { return session; }
    public Google getGoogle() { return google; }
    public Apple getApple() { return apple; }

    public static class Session {
        private long ttlHours = 168;
        public long getTtlHours() { return ttlHours; }
        public void setTtlHours(long ttlHours) { this.ttlHours = ttlHours; }
    }

    public static class Google {
        private List<String> audience = new ArrayList<>();
        public List<String> getAudience() { return audience; }
        public void setAudience(List<String> audience) { this.audience = audience; }
    }

    public static class Apple {
        private List<String> audience = new ArrayList<>();
        public List<String> getAudience() { return audience; }
        public void setAudience(List<String> audience) { this.audience = audience; }
    }
}
