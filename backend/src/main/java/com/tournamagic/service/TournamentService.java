package com.tournamagic.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.tournamagic.auth.AuthService;
import com.tournamagic.dto.*;
import com.tournamagic.supabase.SupabaseClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TournamentService {
    private final SupabaseClient supabaseClient;
    private final AuthService authService;

    public TournamentService(SupabaseClient supabaseClient, AuthService authService) {
        this.supabaseClient = supabaseClient;
        this.authService = authService;
    }

    public TournamentDto createTournament(String accessToken, CreateTournamentRequest request) {
        validatePlayers(request.players());
        String userId = authService.requireUserId(accessToken);

        String tournamentId = UUID.randomUUID().toString();
        Instant createdAt = Instant.now();
        supabaseClient.insert("tournaments", List.of(Map.of(
                "id", tournamentId,
                "user_id", userId,
                "name", request.name().trim(),
                "status", "active",
                "created_at", createdAt.toString()
        )));

        List<Map<String, Object>> playerRows = request.players().stream().map(name -> Map.<String, Object>of(
                "id", UUID.randomUUID().toString(),
                "tournament_id", tournamentId,
                "name", name.trim()
        )).toList();
        supabaseClient.insert("players", playerRows);

        List<PlayerDto> players = playerRows.stream()
                .map(row -> new PlayerDto(row.get("id").toString(), row.get("name").toString()))
                .toList();

        List<Map<String, Object>> matchRows = createRoundRobinMatches(tournamentId, players);
        supabaseClient.insert("matches", matchRows);

        List<MatchDto> matches = matchRows.stream().map(this::mapMatch).toList();
        return new TournamentDto(tournamentId, request.name().trim(), createdAt, "active", players, matches);
    }

    public List<TournamentSummaryDto> listTournaments(String accessToken) {
        String userId = authService.requireUserId(accessToken);
        JsonNode tournaments = supabaseClient.select("tournaments", Map.of("user_id", "eq." + userId), "*");
        List<TournamentSummaryDto> out = new ArrayList<>();
        for (JsonNode t : tournaments) {
            String tournamentId = t.get("id").asText();
            JsonNode players = supabaseClient.select("players", Map.of("tournament_id", "eq." + tournamentId), "id");
            JsonNode matches = supabaseClient.select("matches", Map.of("tournament_id", "eq." + tournamentId), "status");
            int completed = 0;
            for (JsonNode m : matches) {
                if ("completed".equals(m.path("status").asText())) completed++;
            }
            out.add(new TournamentSummaryDto(
                    tournamentId,
                    t.path("name").asText(),
                    t.path("status").asText(),
                    Instant.parse(t.path("created_at").asText()),
                    players.size(),
                    completed,
                    matches.size()
            ));
        }
        return out.stream().sorted((a, b) -> b.createdAt().compareTo(a.createdAt())).toList();
    }

    public TournamentDto getTournament(String accessToken, String id) {
        String userId = authService.requireUserId(accessToken);
        JsonNode tournaments = supabaseClient.select("tournaments", Map.of("id", "eq." + id, "user_id", "eq." + userId), "*");
        if (tournaments.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tournament not found");
        }
        JsonNode tournament = tournaments.get(0);
        List<PlayerDto> players = loadPlayers(id);
        List<MatchDto> matches = loadMatches(id);
        return new TournamentDto(
                id,
                tournament.path("name").asText(),
                Instant.parse(tournament.path("created_at").asText()),
                tournament.path("status").asText(),
                players,
                matches
        );
    }

    public TournamentDto updateMatchResult(String accessToken, String tournamentId, String matchId, UpdateMatchRequest request) {
        if (!isValidBo3(request.winsA(), request.winsB())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Bo3 result. Use 2-0 or 2-1.");
        }
        getTournament(accessToken, tournamentId);
        JsonNode matchRows = supabaseClient.update("matches", Map.of("id", "eq." + matchId, "tournament_id", "eq." + tournamentId), Map.of(
                "wins_a", request.winsA(),
                "wins_b", request.winsB(),
                "status", "completed"
        ));
        if (matchRows.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found");
        }
        JsonNode updated = matchRows.get(0);
        String winnerId = request.winsA() > request.winsB() ? updated.path("player_a_id").asText() : updated.path("player_b_id").asText();
        supabaseClient.update("matches", Map.of("id", "eq." + matchId), Map.of("winner_id", winnerId));
        refreshTournamentStatus(tournamentId);
        return getTournament(accessToken, tournamentId);
    }

    public TournamentDto clearMatchResult(String accessToken, String tournamentId, String matchId) {
        getTournament(accessToken, tournamentId);
        JsonNode rows = supabaseClient.update("matches", Map.of("id", "eq." + matchId, "tournament_id", "eq." + tournamentId), Map.of(
                "status", "pending",
                "wins_a", 0,
                "wins_b", 0,
                "winner_id", null
        ));
        if (rows.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found");
        }
        supabaseClient.update("tournaments", Map.of("id", "eq." + tournamentId), Map.of("status", "active"));
        return getTournament(accessToken, tournamentId);
    }

    public void deleteTournament(String accessToken, String id) {
        getTournament(accessToken, id);
        supabaseClient.delete("matches", Map.of("tournament_id", "eq." + id));
        supabaseClient.delete("players", Map.of("tournament_id", "eq." + id));
        supabaseClient.delete("tournaments", Map.of("id", "eq." + id));
    }

    public List<StandingDto> standings(String accessToken, String tournamentId) {
        getTournament(accessToken, tournamentId);
        List<PlayerDto> players = loadPlayers(tournamentId);
        List<MatchDto> matches = loadMatches(tournamentId);

        Map<String, StandingDtoBuilder> rows = new LinkedHashMap<>();
        players.forEach(player -> rows.put(player.id(), new StandingDtoBuilder(player.id(), player.name())));

        for (MatchDto match : matches) {
            if (!"completed".equals(match.status()) || match.winnerId() == null) continue;
            StandingDtoBuilder a = rows.get(match.playerAId());
            StandingDtoBuilder b = rows.get(match.playerBId());
            a.gameWins += match.winsA();
            a.gameLosses += match.winsB();
            b.gameWins += match.winsB();
            b.gameLosses += match.winsA();
            if (match.winnerId().equals(match.playerAId())) {
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

    private void refreshTournamentStatus(String tournamentId) {
        JsonNode matches = supabaseClient.select("matches", Map.of("tournament_id", "eq." + tournamentId), "status");
        boolean allCompleted = matches.size() > 0;
        for (JsonNode match : matches) {
            if (!"completed".equals(match.path("status").asText())) {
                allCompleted = false;
                break;
            }
        }
        supabaseClient.update("tournaments", Map.of("id", "eq." + tournamentId), Map.of("status", allCompleted ? "complete" : "active"));
    }

    private List<PlayerDto> loadPlayers(String tournamentId) {
        JsonNode players = supabaseClient.select("players", Map.of("tournament_id", "eq." + tournamentId), "*");
        List<PlayerDto> out = new ArrayList<>();
        for (JsonNode p : players) {
            out.add(new PlayerDto(p.path("id").asText(), p.path("name").asText()));
        }
        return out;
    }

    private List<MatchDto> loadMatches(String tournamentId) {
        JsonNode matches = supabaseClient.select("matches", Map.of("tournament_id", "eq." + tournamentId), "*");
        List<MatchDto> out = new ArrayList<>();
        for (JsonNode m : matches) {
            out.add(new MatchDto(
                    m.path("id").asText(),
                    m.path("player_a_id").asText(),
                    m.path("player_b_id").asText(),
                    m.path("status").asText(),
                    m.path("wins_a").asInt(0),
                    m.path("wins_b").asInt(0),
                    m.path("winner_id").isNull() ? null : m.path("winner_id").asText()
            ));
        }
        return out;
    }

    private MatchDto mapMatch(Map<String, Object> row) {
        return new MatchDto(
                row.get("id").toString(),
                row.get("player_a_id").toString(),
                row.get("player_b_id").toString(),
                row.get("status").toString(),
                (int) row.get("wins_a"),
                (int) row.get("wins_b"),
                null
        );
    }

    private List<Map<String, Object>> createRoundRobinMatches(String tournamentId, List<PlayerDto> players) {
        List<Map<String, Object>> matches = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            for (int j = i + 1; j < players.size(); j++) {
                matches.add(Map.of(
                        "id", UUID.randomUUID().toString(),
                        "tournament_id", tournamentId,
                        "player_a_id", players.get(i).id(),
                        "player_b_id", players.get(j).id(),
                        "status", "pending",
                        "wins_a", 0,
                        "wins_b", 0
                ));
            }
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
