package com.tournamagic.repo;

import com.tournamagic.domain.TournamentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TournamentRepository extends JpaRepository<TournamentEntity, String> {
    boolean existsByJoinCode(String joinCode);
    Optional<TournamentEntity> findByJoinCode(String joinCode);
}
