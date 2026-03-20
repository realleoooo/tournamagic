package com.tournamagic.dto;

public record PlayerDto(
        String id,
        String name,
        String claimedByEmail,
        String claimedByName
) {
}
