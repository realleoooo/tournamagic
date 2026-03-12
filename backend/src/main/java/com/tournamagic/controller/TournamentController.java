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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TournamentDto createTournament(@Valid @RequestBody CreateTournamentRequest request) {
        return tournamentService.createTournament(request);
    }

    @GetMapping("/{id}")
    public TournamentDto getTournament(@PathVariable String id) {
        return tournamentService.getTournament(id);
    }

    @GetMapping("/{id}/standings")
    public List<StandingDto> standings(@PathVariable String id) {
        return tournamentService.standings(id);
    }

    @PutMapping("/{id}/matches/{matchId}")
    public TournamentDto submitMatch(@PathVariable String id, @PathVariable String matchId, @RequestBody UpdateMatchRequest request) {
        return tournamentService.updateMatchResult(id, matchId, request);
    }

    @DeleteMapping("/{id}/matches/{matchId}")
    public TournamentDto clearMatch(@PathVariable String id, @PathVariable String matchId) {
        return tournamentService.clearMatchResult(id, matchId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTournament(@PathVariable String id) {
        tournamentService.deleteTournament(id);
    }
}
