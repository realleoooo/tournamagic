package com.tournamagic.dto;

import java.time.Instant;

public record TournamentSummaryDto(
        String id,
        String name,
        String status,
        Instant createdAt,
        int playerCount,
        int completedMatches,
        int totalMatches
) {
}
