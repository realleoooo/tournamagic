package com.tournamagic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateTournamentRequest(
        @NotBlank String name,
        @Size(min = 2) List<@NotBlank String> players
) {
}
