package com.tournamagic.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.tournamagic.auth.config.AuthProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GoogleTokenVerifierService implements SocialTokenVerifier {
    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifierService(AuthProperties properties) {
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(properties.getGoogle().getAudience())
                .build();
    }

    @Override
    public String provider() {
        return "google";
    }

    @Override
    public SocialIdentity verify(String idToken) {
        try {
            GoogleIdToken token = verifier.verify(idToken);
            if (token == null) {
                throw new AuthException("Invalid Google token.");
            }

            GoogleIdToken.Payload payload = token.getPayload();
            String email = payload.getEmail();
            if (email == null || email.isBlank()) {
                throw new AuthException("Google account did not provide an email.");
            }

            String subject = payload.getSubject();
            String name = (String) payload.get("name");
            return new SocialIdentity(provider(), subject, email, name == null || name.isBlank() ? email : name);
        } catch (IOException e) {
            throw new AuthException("Could not validate Google token.");
        }
    }
}
