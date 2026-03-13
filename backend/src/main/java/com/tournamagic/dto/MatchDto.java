package com.tournamagic.dto;

public record MatchDto(
        String id,
        String playerAId,
        String playerBId,
        String status,
        int winsA,
        int winsB,
        String winnerId
) {
}
