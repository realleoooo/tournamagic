package com.tournamagic.dto;

import java.time.Instant;
import java.util.List;

public record TournamentDto(
        String id,
        String name,
        Instant createdAt,
        String status,
        String joinCode,
        boolean joinEnabled,
        Instant joinCodeExpiresAt,
        List<PlayerDto> players,
        List<MatchDto> matches,
        List<JoinedUserDto> participants,
        boolean currentUserJoined
) {
}
