package com.tournamagic.dto;

import java.util.List;

public record PlayerProfileDto(
        String name,
        String email,
        boolean currentUser,
        PlayerProfileStatsDto stats,
        List<PlayerProfileTournamentDto> tournaments
) {
}
