package com.tournamagic.dto;

import java.time.Instant;
import java.util.List;

public record JoinTournamentPreviewDto(
        String tournamentId,
        String tournamentName,
        String joinCode,
        String status,
        boolean joinEnabled,
        Instant joinCodeExpiresAt,
        List<PlayerDto> availablePlayers
) {
}
