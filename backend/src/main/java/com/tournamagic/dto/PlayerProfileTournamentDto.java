package com.tournamagic.dto;

import java.time.Instant;

public record PlayerProfileTournamentDto(
        String tournamentId,
        String tournamentName,
        Instant createdAt,
        String status,
        String playerId,
        String playerName,
        Instant joinedAt,
        int playerCount,
        int completedMatches,
        int totalMatches,
        int matchWins,
        int matchLosses,
        int gameWins,
        int gameLosses,
        Integer placement
) {
}
