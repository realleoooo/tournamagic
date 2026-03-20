package com.tournamagic.dto;

import java.time.Instant;

public record JoinTournamentPreviewDto(
        String tournamentId,
        String tournamentName,
        String joinCode,
        String status,
        boolean joinEnabled,
        Instant joinCodeExpiresAt
) {
}
