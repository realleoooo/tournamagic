package com.tournamagic.repo;

import com.tournamagic.domain.MatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<MatchEntity, String> {
    List<MatchEntity> findByTournamentId(String tournamentId);
    void deleteByTournamentId(String tournamentId);
}
