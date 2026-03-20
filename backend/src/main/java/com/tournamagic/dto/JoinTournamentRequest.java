package com.tournamagic.dto;

import jakarta.validation.constraints.NotBlank;

public record JoinTournamentRequest(
        @NotBlank String code,
        @NotBlank String playerId
) {
}
