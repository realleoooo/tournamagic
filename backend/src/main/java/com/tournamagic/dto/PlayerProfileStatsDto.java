package com.tournamagic.dto;

public record PlayerProfileStatsDto(
        int tournamentsPlayed,
        int completedTournaments,
        int inProgressTournaments,
        int totalMatchWins,
        int totalMatchLosses,
        int totalGameWins,
        int totalGameLosses,
        int firstPlaces,
        int secondPlaces,
        int thirdPlaces
) {
}
