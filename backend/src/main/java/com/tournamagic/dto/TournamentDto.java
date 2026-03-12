package com.tournamagic.dto;

import java.time.Instant;
import java.util.List;

public record TournamentDto(
        String id,
        String name,
        Instant createdAt,
        String status,
        List<PlayerDto> players,
        List<MatchDto> matches
) {
}
