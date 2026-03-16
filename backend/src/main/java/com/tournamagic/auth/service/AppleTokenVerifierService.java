package com.tournamagic.auth.service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.tournamagic.auth.config.AuthProperties;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

@Service
public class AppleTokenVerifierService implements SocialTokenVerifier {
    private static final String APPLE_ISSUER = "https://appleid.apple.com";
    private final ConfigurableJWTProcessor<com.nimbusds.jose.proc.SecurityContext> jwtProcessor;
    private final Set<String> acceptedAudience;

    public AppleTokenVerifierService(AuthProperties properties) {
        this.acceptedAudience = Set.copyOf(properties.getApple().getAudience());
        this.jwtProcessor = new DefaultJWTProcessor<>();
        this.jwtProcessor.setJWSKeySelector(keySelector());
    }

    @Override
    public String provider() {
        return "apple";
    }

    @Override
    public SocialIdentity verify(String idToken) {
        try {
            JWTClaimsSet claims = jwtProcessor.process(idToken, null);
            if (!APPLE_ISSUER.equals(claims.getIssuer())) {
                throw new AuthException("Invalid Apple token issuer.");
            }

            if (acceptedAudience.isEmpty() || claims.getAudience().stream().noneMatch(acceptedAudience::contains)) {
                throw new AuthException("Invalid Apple token audience.");
            }

            Date expiration = claims.getExpirationTime();
            if (expiration == null || expiration.toInstant().isBefore(Instant.now())) {
                throw new AuthException("Apple token is expired.");
            }

            String email = claims.getStringClaim("email");
            if (email == null || email.isBlank()) {
                throw new AuthException("Apple account did not provide an email.");
            }

            String subject = claims.getSubject();
            String name = claims.getStringClaim("name");
            return new SocialIdentity(provider(), subject, email, name == null || name.isBlank() ? email : name);
        } catch (AuthException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AuthException("Could not validate Apple token.");
        }
    }

    private JWSKeySelector<com.nimbusds.jose.proc.SecurityContext> keySelector() {
        try {
            JWKSource<com.nimbusds.jose.proc.SecurityContext> keySource =
                    new RemoteJWKSet<>(URI.create("https://appleid.apple.com/auth/keys").toURL());
            return new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, keySource);
        } catch (Exception ex) {
            throw new IllegalStateException("Could not initialize Apple key source", ex);
        }
    }
}
