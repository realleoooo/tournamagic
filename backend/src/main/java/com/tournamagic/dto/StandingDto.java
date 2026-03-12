package com.tournamagic.dto;

public record StandingDto(
        String playerId,
        String playerName,
        int matchWins,
        int matchLosses,
        int gameWins,
        int gameLosses,
        int gameDiff,
        double matchWinPct,
        double gameWinPct
) {
}
