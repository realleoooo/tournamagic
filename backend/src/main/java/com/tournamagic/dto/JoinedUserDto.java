package com.tournamagic.dto;

import java.time.Instant;

public record JoinedUserDto(
        String email,
        String name,
        String playerId,
        String playerName,
        Instant joinedAt
) {
}
