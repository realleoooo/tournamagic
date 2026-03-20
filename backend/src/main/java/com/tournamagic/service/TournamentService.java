package com.tournamagic.service;

import com.tournamagic.domain.MatchEntity;
import com.tournamagic.domain.PlayerEntity;
import com.tournamagic.domain.TournamentEntity;
import com.tournamagic.domain.TournamentParticipantEntity;
import com.tournamagic.dto.*;
import com.tournamagic.repo.MatchRepository;
import com.tournamagic.repo.PlayerRepository;
import com.tournamagic.repo.TournamentParticipantRepository;
import com.tournamagic.repo.TournamentRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TournamentService {
    private static final String SETUP_STATUS = "setup";
    private static final String ACTIVE_STATUS = "active";
    private static final String COMPLETE_STATUS = "complete";
    private static final String JOIN_CODE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int JOIN_CODE_LENGTH = 8;

    private final TournamentRepository tournamentRepository;
    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;
    private final TournamentParticipantRepository tournamentParticipantRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public TournamentService(
            TournamentRepository tournamentRepository,
            PlayerRepository playerRepository,
            MatchRepository matchRepository,
            TournamentParticipantRepository tournamentParticipantRepository
    ) {
        this.tournamentRepository = tournamentRepository;
        this.playerRepository = playerRepository;
        this.matchRepository = matchRepository;
        this.tournamentParticipantRepository = tournamentParticipantRepository;
    }

    @Transactional
    public TournamentDto createTournament(CreateTournamentRequest request, AuthenticatedUser currentUser) {
        TournamentEntity tournament = new TournamentEntity();
        tournament.setId(UUID.randomUUID().toString());
        tournament.setName(request.name().trim());
        tournament.setStatus(SETUP_STATUS);
        tournament.setCreatedAt(Instant.now());
        tournament.setJoinCode(generateJoinCode());
        tournament.setJoinEnabled(true);
        tournament.setJoinCodeExpiresAt(null);
        tournamentRepository.save(tournament);

        if (currentUser != null) {
            addParticipantIfMissing(tournament, currentUser);
        }

        return getTournament(tournament.getId(), currentUser);
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

    public TournamentDto getTournament(String id, AuthenticatedUser currentUser) {
        TournamentEntity tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tournament not found"));
        List<PlayerEntity> players = playerRepository.findByTournamentId(id);
        List<MatchEntity> matches = matchRepository.findByTournamentId(id);
        List<TournamentParticipantEntity> participants = tournamentParticipantRepository.findByTournamentIdOrderByJoinedAtAsc(id);
        return toDto(tournament, players, matches, participants, currentUser);
    }

    public JoinTournamentPreviewDto previewJoin(String code) {
        TournamentEntity tournament = findTournamentByCode(code);
        return new JoinTournamentPreviewDto(
                tournament.getId(),
                tournament.getName(),
                tournament.getJoinCode(),
                tournament.getStatus(),
                tournament.isJoinEnabled(),
                tournament.getJoinCodeExpiresAt()
        );
    }

    @Transactional
    public TournamentDto joinTournament(String code, AuthenticatedUser currentUser) {
        TournamentEntity tournament = findTournamentByCode(code);
        addParticipantIfMissing(tournament, currentUser);
        return getTournament(tournament.getId(), currentUser);
    }

    @Transactional
    public TournamentDto leaveTournament(String tournamentId, AuthenticatedUser currentUser) {
        TournamentEntity tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tournament not found"));
        assertSetupRosterOpen(tournament, "You can only leave before the tournament starts.");

        TournamentParticipantEntity participant = tournamentParticipantRepository
                .findByTournamentIdAndUserEmail(tournamentId, currentUser.email().trim().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "You are not part of this tournament."));

        tournamentParticipantRepository.deleteById(participant.getId());
        playerRepository.deleteById(participant.getId());

        return getTournament(tournamentId, currentUser);
    }

    @Transactional
    public TournamentDto startTournament(String tournamentId, AuthenticatedUser currentUser) {
        TournamentEntity tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tournament not found"));
        assertSetupRosterOpen(tournament, "This tournament has already started.");

        List<PlayerEntity> players = playerRepository.findByTournamentId(tournamentId);
        if (players.size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least 2 joined players are required to start the tournament.");
        }

        if (!matchRepository.findByTournamentId(tournamentId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Matches have already been generated for this tournament.");
        }

        List<MatchEntity> matches = createRoundRobinMatches(tournamentId, players);
        matchRepository.saveAll(matches);
        tournament.setStatus(ACTIVE_STATUS);
        tournament.setJoinEnabled(false);
        tournamentRepository.save(tournament);

        return getTournament(tournamentId, currentUser);
    }

    @Transactional
    public TournamentDto updateMatchResult(String tournamentId, String matchId, UpdateMatchRequest request, AuthenticatedUser currentUser) {
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
        tournament.setStatus(allCompleted ? COMPLETE_STATUS : ACTIVE_STATUS);
        tournamentRepository.save(tournament);

        return getTournament(tournamentId, currentUser);
    }

    @Transactional
    public TournamentDto clearMatchResult(String tournamentId, String matchId, AuthenticatedUser currentUser) {
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

        tournament.setStatus(ACTIVE_STATUS);
        tournamentRepository.save(tournament);

        return getTournament(tournamentId, currentUser);
    }

    @Transactional
    public void deleteTournament(String tournamentId) {
        if (!tournamentRepository.existsById(tournamentId)) {
            return;
        }
        matchRepository.deleteByTournamentId(tournamentId);
        playerRepository.deleteByTournamentId(tournamentId);
        tournamentParticipantRepository.deleteByTournamentId(tournamentId);
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

    private TournamentEntity findTournamentByCode(String rawCode) {
        String normalizedCode = normalizeJoinCode(rawCode);
        TournamentEntity tournament = tournamentRepository.findByJoinCode(normalizedCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Join code is invalid."));
        if (tournament.getJoinCodeExpiresAt() != null && tournament.getJoinCodeExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.GONE, "Join code has expired.");
        }
        return tournament;
    }

    private void assertSetupRosterOpen(TournamentEntity tournament, String message) {
        if (!tournament.isJoinEnabled() || !SETUP_STATUS.equals(tournament.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, message);
        }
    }

    private void addParticipantIfMissing(TournamentEntity tournament, AuthenticatedUser currentUser) {
        assertSetupRosterOpen(tournament, "This tournament is no longer accepting players.");

        String email = currentUser.email().trim().toLowerCase();
        if (tournamentParticipantRepository.findByTournamentIdAndUserEmail(tournament.getId(), email).isPresent()) {
            return;
        }

        String participantId = UUID.randomUUID().toString();

        TournamentParticipantEntity participant = new TournamentParticipantEntity();
        participant.setId(participantId);
        participant.setTournamentId(tournament.getId());
        participant.setUserEmail(email);
        participant.setUserName(currentUser.name().trim());
        participant.setJoinedAt(Instant.now());
        tournamentParticipantRepository.save(participant);

        PlayerEntity player = new PlayerEntity();
        player.setId(participantId);
        player.setTournamentId(tournament.getId());
        player.setName(currentUser.name().trim());
        playerRepository.save(player);
    }

    private String generateJoinCode() {
        String generated;
        do {
            StringBuilder builder = new StringBuilder(JOIN_CODE_LENGTH);
            for (int i = 0; i < JOIN_CODE_LENGTH; i++) {
                int index = secureRandom.nextInt(JOIN_CODE_ALPHABET.length());
                builder.append(JOIN_CODE_ALPHABET.charAt(index));
            }
            generated = builder.toString();
        } while (tournamentRepository.existsByJoinCode(generated));
        return generated;
    }

    private String normalizeJoinCode(String code) {
        return code == null ? "" : code.trim().toUpperCase(Locale.ROOT);
    }

    private List<MatchEntity> createRoundRobinMatches(String tournamentId, List<PlayerEntity> players) {
        List<MatchEntity> matches = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            for (int j = i + 1; j < players.size(); j++) {
                MatchEntity match = new MatchEntity();
                match.setId(UUID.randomUUID().toString());
                match.setTournamentId(tournamentId);
                match.setPlayerAId(players.get(i).getId());
                match.setPlayerBId(players.get(j).getId());
                match.setStatus("pending");
                match.setWinsA(0);
                match.setWinsB(0);
                matches.add(match);
            }
        }
        return matches;
    }

    private boolean isValidBo3(int winsA, int winsB) {
        int total = winsA + winsB;
        return (winsA == 2 || winsB == 2) && total >= 2 && total <= 3;
    }

    private TournamentDto toDto(
            TournamentEntity tournament,
            List<PlayerEntity> players,
            List<MatchEntity> matches,
            List<TournamentParticipantEntity> participants,
            AuthenticatedUser currentUser
    ) {
        List<PlayerDto> playerDtos = players.stream().map(p -> new PlayerDto(p.getId(), p.getName())).toList();
        List<MatchDto> matchDtos = matches.stream().map(m -> new MatchDto(
                m.getId(), m.getPlayerAId(), m.getPlayerBId(), m.getStatus(), m.getWinsA(), m.getWinsB(), m.getWinnerId()
        )).toList();
        List<JoinedUserDto> participantDtos = participants.stream()
                .map(p -> new JoinedUserDto(p.getUserEmail(), p.getUserName(), p.getJoinedAt()))
                .toList();
        boolean currentUserJoined = currentUser != null && participants.stream()
                .anyMatch(participant -> participant.getUserEmail().equalsIgnoreCase(currentUser.email()));

        return new TournamentDto(
                tournament.getId(),
                tournament.getName(),
                tournament.getCreatedAt(),
                tournament.getStatus(),
                tournament.getJoinCode(),
                tournament.isJoinEnabled(),
                tournament.getJoinCodeExpiresAt(),
                playerDtos,
                matchDtos,
                participantDtos,
                currentUserJoined
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
