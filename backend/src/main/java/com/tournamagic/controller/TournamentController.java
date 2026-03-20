package com.tournamagic.controller;

import com.tournamagic.dto.*;
import com.tournamagic.service.TournamentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {
    private static final String AUTH_NAME_HEADER = "X-Auth-User-Name";
    private static final String AUTH_EMAIL_HEADER = "X-Auth-User-Email";

    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @GetMapping
    public List<TournamentSummaryDto> listTournaments(
            @RequestHeader(value = AUTH_NAME_HEADER, required = false) String authName,
            @RequestHeader(value = AUTH_EMAIL_HEADER, required = false) String authEmail
    ) {
        return tournamentService.listTournaments(requiredUser(authName, authEmail));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TournamentDto createTournament(
            @Valid @RequestBody CreateTournamentRequest request,
            @RequestHeader(value = AUTH_NAME_HEADER, required = false) String authName,
            @RequestHeader(value = AUTH_EMAIL_HEADER, required = false) String authEmail
    ) {
        return tournamentService.createTournament(request, requiredUser(authName, authEmail));
    }

    @GetMapping("/{id}")
    public TournamentDto getTournament(
            @PathVariable String id,
            @RequestHeader(value = AUTH_NAME_HEADER, required = false) String authName,
            @RequestHeader(value = AUTH_EMAIL_HEADER, required = false) String authEmail
    ) {
        return tournamentService.getTournament(id, requiredUser(authName, authEmail));
    }

    @PostMapping("/{id}/start")
    public TournamentDto startTournament(
            @PathVariable String id,
            @RequestHeader(value = AUTH_NAME_HEADER, required = false) String authName,
            @RequestHeader(value = AUTH_EMAIL_HEADER, required = false) String authEmail
    ) {
        return tournamentService.startTournament(id, requiredUser(authName, authEmail));
    }

    @DeleteMapping("/{id}/participants/me")
    public TournamentDto leaveTournament(
            @PathVariable String id,
            @RequestHeader(value = AUTH_NAME_HEADER, required = false) String authName,
            @RequestHeader(value = AUTH_EMAIL_HEADER, required = false) String authEmail
    ) {
        return tournamentService.leaveTournament(id, requiredUser(authName, authEmail));
    }

    @GetMapping("/join/{code}")
    public JoinTournamentPreviewDto previewJoin(@PathVariable String code) {
        return tournamentService.previewJoin(code);
    }

    @PostMapping("/join")
    public TournamentDto joinTournament(
            @Valid @RequestBody JoinTournamentRequest request,
            @RequestHeader(value = AUTH_NAME_HEADER, required = false) String authName,
            @RequestHeader(value = AUTH_EMAIL_HEADER, required = false) String authEmail
    ) {
        return tournamentService.joinTournament(request.code(), request.playerId(), requiredUser(authName, authEmail));
    }

    @GetMapping("/{id}/standings")
    public List<StandingDto> standings(
            @PathVariable String id,
            @RequestHeader(value = AUTH_NAME_HEADER, required = false) String authName,
            @RequestHeader(value = AUTH_EMAIL_HEADER, required = false) String authEmail
    ) {
        tournamentService.getTournament(id, requiredUser(authName, authEmail));
        return tournamentService.standings(id);
    }

    @PutMapping("/{id}/matches/{matchId}")
    public TournamentDto submitMatch(
            @PathVariable String id,
            @PathVariable String matchId,
            @RequestBody UpdateMatchRequest request,
            @RequestHeader(value = AUTH_NAME_HEADER, required = false) String authName,
            @RequestHeader(value = AUTH_EMAIL_HEADER, required = false) String authEmail
    ) {
        return tournamentService.updateMatchResult(id, matchId, request, requiredUser(authName, authEmail));
    }

    @DeleteMapping("/{id}/matches/{matchId}")
    public TournamentDto clearMatch(
            @PathVariable String id,
            @PathVariable String matchId,
            @RequestHeader(value = AUTH_NAME_HEADER, required = false) String authName,
            @RequestHeader(value = AUTH_EMAIL_HEADER, required = false) String authEmail
    ) {
        return tournamentService.clearMatchResult(id, matchId, requiredUser(authName, authEmail));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTournament(@PathVariable String id) {
        tournamentService.deleteTournament(id);
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
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to manage tournament membership.");
        }
        return user;
    }
}
