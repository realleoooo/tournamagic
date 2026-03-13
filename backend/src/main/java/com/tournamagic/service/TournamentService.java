package com.tournamagic.service;

import com.tournamagic.domain.MatchEntity;
import com.tournamagic.domain.PlayerEntity;
import com.tournamagic.domain.TournamentEntity;
import com.tournamagic.dto.*;
import com.tournamagic.repo.MatchRepository;
import com.tournamagic.repo.PlayerRepository;
import com.tournamagic.repo.TournamentRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;

    public TournamentService(TournamentRepository tournamentRepository, PlayerRepository playerRepository, MatchRepository matchRepository) {
        this.tournamentRepository = tournamentRepository;
        this.playerRepository = playerRepository;
        this.matchRepository = matchRepository;
    }

    @Transactional
    public TournamentDto createTournament(CreateTournamentRequest request) {
        validatePlayers(request.players());

        TournamentEntity tournament = new TournamentEntity();
        tournament.setId(UUID.randomUUID().toString());
        tournament.setName(request.name().trim());
        tournament.setStatus("active");
        tournament.setCreatedAt(Instant.now());
        tournamentRepository.save(tournament);

        List<PlayerEntity> players = request.players().stream().map(name -> {
            PlayerEntity p = new PlayerEntity();
            p.setId(UUID.randomUUID().toString());
            p.setTournamentId(tournament.getId());
            p.setName(name.trim());
            return p;
        }).toList();
        playerRepository.saveAll(players);

        List<MatchEntity> matches = createRoundRobinMatches(tournament.getId(), players);
        matchRepository.saveAll(matches);

        return toDto(tournament, players, matches);
    }

    public List<TournamentSummaryDto> listTournaments() {
        List<TournamentEntity> tournaments = tournamentRepository.findAll();
        return tournaments.stream().map(tournament -> {
            List<PlayerEntity> players = playerRepository.findByTournamentId(tournament.getId());
            List<MatchEntity> matches = matchRepository.findByTournamentId(tournament.getId());
            int completed = (int) matches.stream().filter(m -> "completed".equals(m.getStatus())).count();
            return new TournamentSummaryDto(
                    tournament.getId(),
                    tournament.getName(),
                    tournament.getStatus(),
                    tournament.getCreatedAt(),
                    players.size(),
                    completed,
                    matches.size()
            );
        }).sorted((a, b) -> b.createdAt().compareTo(a.createdAt())).toList();
    }

    public TournamentDto getTournament(String id) {
        TournamentEntity tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tournament not found"));
        List<PlayerEntity> players = playerRepository.findByTournamentId(id);
        List<MatchEntity> matches = matchRepository.findByTournamentId(id);
        return toDto(tournament, players, matches);
    }

    @Transactional
    public TournamentDto updateMatchResult(String tournamentId, String matchId, UpdateMatchRequest request) {
        if (!isValidBo3(request.winsA(), request.winsB())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Bo3 result. Use 2-0 or 2-1.");
        }

        TournamentEntity tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tournament not found"));
        MatchEntity match = matchRepository.findById(matchId)
                .filter(m -> m.getTournamentId().equals(tournamentId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found"));

        match.setWinsA(request.winsA());
        match.setWinsB(request.winsB());
        match.setStatus("completed");
        match.setWinnerId(request.winsA() > request.winsB() ? match.getPlayerAId() : match.getPlayerBId());
        matchRepository.save(match);

        boolean allCompleted = matchRepository.findByTournamentId(tournamentId).stream()
                .allMatch(m -> "completed".equals(m.getStatus()));
        tournament.setStatus(allCompleted ? "complete" : "active");
        tournamentRepository.save(tournament);

        return getTournament(tournamentId);
    }

    @Transactional
    public TournamentDto clearMatchResult(String tournamentId, String matchId) {
        TournamentEntity tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tournament not found"));
        MatchEntity match = matchRepository.findById(matchId)
                .filter(m -> m.getTournamentId().equals(tournamentId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found"));

        match.setStatus("pending");
        match.setWinsA(0);
        match.setWinsB(0);
        match.setWinnerId(null);
        matchRepository.save(match);

        tournament.setStatus("active");
        tournamentRepository.save(tournament);

        return getTournament(tournamentId);
    }

    @Transactional
    public void deleteTournament(String tournamentId) {
        if (!tournamentRepository.existsById(tournamentId)) {
            return;
        }
        matchRepository.deleteByTournamentId(tournamentId);
        playerRepository.deleteByTournamentId(tournamentId);
        tournamentRepository.deleteById(tournamentId);
    }

    public List<StandingDto> standings(String tournamentId) {
        List<PlayerEntity> players = playerRepository.findByTournamentId(tournamentId);
        List<MatchEntity> matches = matchRepository.findByTournamentId(tournamentId);

        Map<String, StandingDtoBuilder> rows = new HashMap<>();
        players.forEach(p -> rows.put(p.getId(), new StandingDtoBuilder(p.getId(), p.getName())));

        for (MatchEntity match : matches) {
            if (!"completed".equals(match.getStatus()) || match.getWinnerId() == null) continue;
            StandingDtoBuilder a = rows.get(match.getPlayerAId());
            StandingDtoBuilder b = rows.get(match.getPlayerBId());
            a.gameWins += match.getWinsA();
            a.gameLosses += match.getWinsB();
            b.gameWins += match.getWinsB();
            b.gameLosses += match.getWinsA();
            if (match.getWinnerId().equals(match.getPlayerAId())) {
                a.matchWins++;
                b.matchLosses++;
            } else {
                b.matchWins++;
                a.matchLosses++;
            }
        }

        List<StandingDto> standings = rows.values().stream().map(StandingDtoBuilder::build).collect(Collectors.toList());
        standings.sort((a, b) -> {
            if (b.matchWins() != a.matchWins()) return Integer.compare(b.matchWins(), a.matchWins());
            if (b.gameDiff() != a.gameDiff()) return Integer.compare(b.gameDiff(), a.gameDiff());
            if (b.gameWins() != a.gameWins()) return Integer.compare(b.gameWins(), a.gameWins());
            return a.playerName().compareToIgnoreCase(b.playerName());
        });
        return standings;
    }

    private List<MatchEntity> createRoundRobinMatches(String tournamentId, List<PlayerEntity> players) {
        List<MatchEntity> matches = new ArrayList<>();

        List<PlayerEntity> rotation = new ArrayList<>(players);
        if (rotation.size() % 2 != 0) {
            rotation.add(null);
        }

        int playerSlots = rotation.size();
        int rounds = playerSlots - 1;
        int matchesPerRound = playerSlots / 2;

        for (int round = 1; round <= rounds; round++) {
            for (int i = 0; i < matchesPerRound; i++) {
                PlayerEntity a = rotation.get(i);
                PlayerEntity b = rotation.get(playerSlots - 1 - i);

                if (a == null || b == null) {
                    continue;
                }

                MatchEntity match = new MatchEntity();
                match.setId(UUID.randomUUID().toString());
                match.setTournamentId(tournamentId);
                match.setPlayerAId(a.getId());
                match.setPlayerBId(b.getId());
                match.setRoundNumber(round);
                match.setStatus("pending");
                match.setWinsA(0);
                match.setWinsB(0);
                matches.add(match);
            }

            PlayerEntity last = rotation.remove(playerSlots - 1);
            rotation.add(1, last);
        }

        return matches;
    }

    private void validatePlayers(List<String> playerNames) {
        Set<String> dedupe = new HashSet<>();
        for (String name : playerNames) {
            String n = name.trim().toLowerCase();
            if (!dedupe.add(n)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate player name: " + name);
            }
        }
    }

    private boolean isValidBo3(int winsA, int winsB) {
        int total = winsA + winsB;
        return (winsA == 2 || winsB == 2) && total >= 2 && total <= 3;
    }

    private TournamentDto toDto(TournamentEntity tournament, List<PlayerEntity> players, List<MatchEntity> matches) {
        List<PlayerDto> playerDtos = players.stream().map(p -> new PlayerDto(p.getId(), p.getName())).toList();
        List<MatchDto> matchDtos = matches.stream()
                .sorted(Comparator.comparingInt(MatchEntity::getRoundNumber).thenComparing(MatchEntity::getId))
                .map(m -> new MatchDto(
                        m.getId(),
                        m.getPlayerAId(),
                        m.getPlayerBId(),
                        m.getRoundNumber(),
                        m.getStatus(),
                        m.getWinsA(),
                        m.getWinsB(),
                        m.getWinnerId()
                )).toList();

        return new TournamentDto(
                tournament.getId(),
                tournament.getName(),
                tournament.getCreatedAt(),
                tournament.getStatus(),
                playerDtos,
                matchDtos
        );
    }

    private static class StandingDtoBuilder {
        private final String playerId;
        private final String playerName;
        private int matchWins;
        private int matchLosses;
        private int gameWins;
        private int gameLosses;

        private StandingDtoBuilder(String playerId, String playerName) {
            this.playerId = playerId;
            this.playerName = playerName;
        }

        private StandingDto build() {
            int gameDiff = gameWins - gameLosses;
            double matchWinPct = matchWins + matchLosses == 0 ? 0 : (double) matchWins / (matchWins + matchLosses);
            double gameWinPct = gameWins + gameLosses == 0 ? 0 : (double) gameWins / (gameWins + gameLosses);
            return new StandingDto(playerId, playerName, matchWins, matchLosses, gameWins, gameLosses, gameDiff, matchWinPct, gameWinPct);
        }
    }
}
