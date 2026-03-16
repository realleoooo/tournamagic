package com.tournamagic.auth.service;

import com.tournamagic.auth.config.AuthProperties;
import com.tournamagic.auth.domain.OAuthAccountEntity;
import com.tournamagic.auth.domain.SessionEntity;
import com.tournamagic.auth.domain.UserEntity;
import com.tournamagic.auth.dto.AuthDtos;
import com.tournamagic.auth.repo.OAuthAccountRepository;
import com.tournamagic.auth.repo.SessionRepository;
import com.tournamagic.auth.repo.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final OAuthAccountRepository oAuthAccountRepository;
    private final SessionRepository sessionRepository;
    private final Map<String, SocialTokenVerifier> verifiers;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final AuthProperties properties;

    public AuthService(
            UserRepository userRepository,
            OAuthAccountRepository oAuthAccountRepository,
            SessionRepository sessionRepository,
            List<SocialTokenVerifier> verifiers,
            AuthProperties properties
    ) {
        this.userRepository = userRepository;
        this.oAuthAccountRepository = oAuthAccountRepository;
        this.sessionRepository = sessionRepository;
        this.verifiers = verifiers.stream().collect(Collectors.toMap(SocialTokenVerifier::provider, Function.identity()));
        this.properties = properties;
    }

    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new AuthException("An account with this email already exists.");
        }

        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID().toString());
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setCreatedAt(Instant.now());
        userRepository.save(user);

        return createSessionResponse(user, true);
    }

    @Transactional
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        String email = normalizeEmail(request.email());
        UserEntity user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new AuthException("Incorrect email or password."));

        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new AuthException("Incorrect email or password.");
        }

        return createSessionResponse(user, false);
    }

    @Transactional
    public AuthDtos.AuthResponse socialLogin(AuthDtos.SocialLoginRequest request) {
        String provider = request.provider().trim().toLowerCase(Locale.ROOT);
        SocialTokenVerifier verifier = verifiers.get(provider);
        if (verifier == null) {
            throw new AuthException("Unsupported social login provider.");
        }

        SocialIdentity identity = verifier.verify(request.idToken());
        String email = normalizeEmail(identity.email());

        OAuthAccountEntity existingOAuth = oAuthAccountRepository
                .findByProviderAndProviderUserId(provider, identity.providerUserId())
                .orElse(null);

        UserEntity user;
        boolean created = false;
        if (existingOAuth != null) {
            user = userRepository.findById(existingOAuth.getUserId())
                    .orElseThrow(() -> new AuthException("Account mapping is invalid."));
        } else {
            user = userRepository.findByEmailIgnoreCase(email).orElse(null);

            if (user == null) {
                user = new UserEntity();
                user.setId(UUID.randomUUID().toString());
                user.setName(identity.name().trim());
                user.setEmail(email);
                user.setCreatedAt(Instant.now());
                userRepository.save(user);
                created = true;
            }

            OAuthAccountEntity linkedAccount = oAuthAccountRepository.findByUserIdAndProvider(user.getId(), provider).orElse(null);
            if (linkedAccount == null) {
                OAuthAccountEntity account = new OAuthAccountEntity();
                account.setId(UUID.randomUUID().toString());
                account.setUserId(user.getId());
                account.setProvider(provider);
                account.setProviderUserId(identity.providerUserId());
                account.setCreatedAt(Instant.now());
                oAuthAccountRepository.save(account);
            }
        }

        return createSessionResponse(user, created);
    }

    @Transactional(readOnly = true)
    public UserEntity requireUserByToken(String token) {
        SessionEntity session = sessionRepository.findById(token)
                .orElseThrow(() -> new AuthException("Unauthorized"));

        if (session.getExpiresAt().isBefore(Instant.now())) {
            throw new AuthException("Session expired");
        }

        return userRepository.findById(session.getUserId())
                .orElseThrow(() -> new AuthException("Unauthorized"));
    }

    @Transactional
    public void logout(String token) {
        sessionRepository.deleteById(token);
    }

    private AuthDtos.AuthResponse createSessionResponse(UserEntity user, boolean created) {
        Instant now = Instant.now();
        SessionEntity session = new SessionEntity();
        session.setToken(UUID.randomUUID().toString().replace("-", ""));
        session.setUserId(user.getId());
        session.setCreatedAt(now);
        session.setExpiresAt(now.plus(properties.getSession().getTtlHours(), ChronoUnit.HOURS));
        sessionRepository.save(session);

        return new AuthDtos.AuthResponse(
                session.getToken(),
                new AuthDtos.AuthUserDto(user.getName(), user.getEmail()),
                created
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
