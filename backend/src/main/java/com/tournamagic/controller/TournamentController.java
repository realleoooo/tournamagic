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
    public List<TournamentSummaryDto> listTournaments() {
        return tournamentService.listTournaments();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TournamentDto createTournament(
            @Valid @RequestBody CreateTournamentRequest request,
            @RequestHeader(value = AUTH_NAME_HEADER, required = false) String authName,
            @RequestHeader(value = AUTH_EMAIL_HEADER, required = false) String authEmail
    ) {
        return tournamentService.createTournament(request, optionalUser(authName, authEmail));
    }

    @GetMapping("/{id}")
    public TournamentDto getTournament(
            @PathVariable String id,
            @RequestHeader(value = AUTH_NAME_HEADER, required = false) String authName,
            @RequestHeader(value = AUTH_EMAIL_HEADER, required = false) String authEmail
    ) {
        return tournamentService.getTournament(id, optionalUser(authName, authEmail));
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
        return tournamentService.joinTournament(request.code(), requiredUser(authName, authEmail));
    }

    @GetMapping("/{id}/standings")
    public List<StandingDto> standings(@PathVariable String id) {
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
        return tournamentService.updateMatchResult(id, matchId, request, optionalUser(authName, authEmail));
    }

    @DeleteMapping("/{id}/matches/{matchId}")
    public TournamentDto clearMatch(
            @PathVariable String id,
            @PathVariable String matchId,
            @RequestHeader(value = AUTH_NAME_HEADER, required = false) String authName,
            @RequestHeader(value = AUTH_EMAIL_HEADER, required = false) String authEmail
    ) {
        return tournamentService.clearMatchResult(id, matchId, optionalUser(authName, authEmail));
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
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to join a tournament.");
        }
        return user;
    }
}
