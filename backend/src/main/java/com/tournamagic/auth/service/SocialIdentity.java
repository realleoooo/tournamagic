package com.tournamagic.auth.service;

public record SocialIdentity(String provider, String providerUserId, String email, String name) {
}
