package com.tournamagic.repo;

import com.tournamagic.domain.TournamentParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TournamentParticipantRepository extends JpaRepository<TournamentParticipantEntity, String> {
    List<TournamentParticipantEntity> findByTournamentIdOrderByJoinedAtAsc(String tournamentId);
    Optional<TournamentParticipantEntity> findByTournamentIdAndUserEmail(String tournamentId, String userEmail);
    void deleteByTournamentId(String tournamentId);
}
