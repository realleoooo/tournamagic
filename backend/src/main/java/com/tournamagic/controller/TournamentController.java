package com.tournamagic.controller;

import com.tournamagic.dto.*;
import com.tournamagic.service.TournamentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {
    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @GetMapping
    public List<TournamentSummaryDto> listTournaments(@RequestHeader("Authorization") String authorization) {
        return tournamentService.listTournaments(token(authorization));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TournamentDto createTournament(@RequestHeader("Authorization") String authorization, @Valid @RequestBody CreateTournamentRequest request) {
        return tournamentService.createTournament(token(authorization), request);
    }

    @GetMapping("/{id}")
    public TournamentDto getTournament(@RequestHeader("Authorization") String authorization, @PathVariable String id) {
        return tournamentService.getTournament(token(authorization), id);
    }

    @GetMapping("/{id}/standings")
    public List<StandingDto> standings(@RequestHeader("Authorization") String authorization, @PathVariable String id) {
        return tournamentService.standings(token(authorization), id);
    }

    @PutMapping("/{id}/matches/{matchId}")
    public TournamentDto submitMatch(@RequestHeader("Authorization") String authorization, @PathVariable String id, @PathVariable String matchId, @RequestBody UpdateMatchRequest request) {
        return tournamentService.updateMatchResult(token(authorization), id, matchId, request);
    }

    @DeleteMapping("/{id}/matches/{matchId}")
    public TournamentDto clearMatch(@RequestHeader("Authorization") String authorization, @PathVariable String id, @PathVariable String matchId) {
        return tournamentService.clearMatchResult(token(authorization), id, matchId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTournament(@RequestHeader("Authorization") String authorization, @PathVariable String id) {
        tournamentService.deleteTournament(token(authorization), id);
    }

    private String token(String authorization) {
        if (authorization == null) return "";
        return authorization.replaceFirst("^Bearer\\s+", "").trim();
    }
}
