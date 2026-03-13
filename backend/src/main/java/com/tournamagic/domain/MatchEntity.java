package com.tournamagic.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "matches")
public class MatchEntity {
    @Id
    private String id;

    @Column(name = "tournament_id", nullable = false)
    private String tournamentId;

    @Column(name = "player_a_id", nullable = false)
    private String playerAId;

    @Column(name = "player_b_id", nullable = false)
    private String playerBId;

    @Column(name = "round_number", nullable = false)
    private int roundNumber;

    @Column(nullable = false)
    private String status;

    @Column(name = "wins_a", nullable = false)
    private int winsA;

    @Column(name = "wins_b", nullable = false)
    private int winsB;

    @Column(name = "winner_id")
    private String winnerId;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTournamentId() { return tournamentId; }
    public void setTournamentId(String tournamentId) { this.tournamentId = tournamentId; }
    public String getPlayerAId() { return playerAId; }
    public void setPlayerAId(String playerAId) { this.playerAId = playerAId; }
    public String getPlayerBId() { return playerBId; }
    public void setPlayerBId(String playerBId) { this.playerBId = playerBId; }
    public int getRoundNumber() { return roundNumber; }
    public void setRoundNumber(int roundNumber) { this.roundNumber = roundNumber; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getWinsA() { return winsA; }
    public void setWinsA(int winsA) { this.winsA = winsA; }
    public int getWinsB() { return winsB; }
    public void setWinsB(int winsB) { this.winsB = winsB; }
    public String getWinnerId() { return winnerId; }
    public void setWinnerId(String winnerId) { this.winnerId = winnerId; }
}
