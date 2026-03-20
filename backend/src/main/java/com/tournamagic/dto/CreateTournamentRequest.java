package com.tournamagic.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateTournamentRequest(
        @NotBlank String name
) {
}
