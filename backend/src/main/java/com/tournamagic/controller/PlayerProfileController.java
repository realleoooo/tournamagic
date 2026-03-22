package com.tournamagic.controller;

import com.tournamagic.dto.AuthenticatedUser;
import com.tournamagic.dto.PlayerProfileDto;
import com.tournamagic.service.TournamentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/players")
public class PlayerProfileController {
    private static final String AUTH_NAME_HEADER = "X-Auth-User-Name";
    private static final String AUTH_EMAIL_HEADER = "X-Auth-User-Email";

    private final TournamentService tournamentService;

    public PlayerProfileController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @GetMapping("/{email}/profile")
    public PlayerProfileDto getProfile(
            @PathVariable String email,
            @RequestHeader(value = AUTH_NAME_HEADER, required = false) String authName,
            @RequestHeader(value = AUTH_EMAIL_HEADER, required = false) String authEmail
    ) {
        return tournamentService.getPlayerProfile(email, requiredUser(authName, authEmail));
    }

    private AuthenticatedUser optionalUser(String authName, String authEmail) {
        if (authName == null || authName.isBlank() || authEmail == null || authEmail.isBlank()) {
            return null;
        }
        return new AuthenticatedUser(authName.trim(), authEmail.trim().toLowerCase());
    }

    private AuthenticatedUser requiredUser(String authName, String authEmail) {
        AuthenticatedUser user = optionalUser(authName, authEmail);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to view player profiles.");
        }
        return user;
    }
}
