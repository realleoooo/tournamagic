package com.tournamagic.repo;

import com.tournamagic.domain.TournamentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentRepository extends JpaRepository<TournamentEntity, String> {
}
