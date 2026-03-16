package com.tournamagic.auth.repo;

import com.tournamagic.auth.domain.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<SessionEntity, String> {
}
