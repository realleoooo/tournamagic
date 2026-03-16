package com.tournamagic.auth.service;

public interface SocialTokenVerifier {
    String provider();
    SocialIdentity verify(String idToken);
}
