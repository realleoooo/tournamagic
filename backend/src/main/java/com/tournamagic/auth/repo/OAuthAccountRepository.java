package com.tournamagic.auth.repo;

import com.tournamagic.auth.domain.OAuthAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthAccountRepository extends JpaRepository<OAuthAccountEntity, String> {
    Optional<OAuthAccountEntity> findByProviderAndProviderUserId(String provider, String providerUserId);
    Optional<OAuthAccountEntity> findByUserIdAndProvider(String userId, String provider);
}
