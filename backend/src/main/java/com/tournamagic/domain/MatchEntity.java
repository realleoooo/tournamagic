package com.tournamagic.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

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

    @Column(nullable = false)
    private String status;

    @Column(name = "wins_a", nullable = false)
    private int winsA;

    @Column(name = "wins_b", nullable = false)
    private int winsB;

    @Column(name = "winner_id")
    private String winnerId;

    @Column(name = "timer_direction", nullable = false)
    private String timerDirection;

    @Column(name = "timer_duration_seconds", nullable = false)
    private int timerDurationSeconds;

    @Column(name = "timer_notify_interval_seconds", nullable = false)
    private int timerNotifyIntervalSeconds;

    @Column(name = "timer_running", nullable = false)
    private boolean timerRunning;

    @Column(name = "timer_started_at")
    private Instant timerStartedAt;

    @Column(name = "timer_elapsed_seconds", nullable = false)
    private int timerElapsedSeconds;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTournamentId() { return tournamentId; }
    public void setTournamentId(String tournamentId) { this.tournamentId = tournamentId; }
    public String getPlayerAId() { return playerAId; }
    public void setPlayerAId(String playerAId) { this.playerAId = playerAId; }
    public String getPlayerBId() { return playerBId; }
    public void setPlayerBId(String playerBId) { this.playerBId = playerBId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getWinsA() { return winsA; }
    public void setWinsA(int winsA) { this.winsA = winsA; }
    public int getWinsB() { return winsB; }
    public void setWinsB(int winsB) { this.winsB = winsB; }
    public String getWinnerId() { return winnerId; }
    public void setWinnerId(String winnerId) { this.winnerId = winnerId; }
    public String getTimerDirection() { return timerDirection; }
    public void setTimerDirection(String timerDirection) { this.timerDirection = timerDirection; }
    public int getTimerDurationSeconds() { return timerDurationSeconds; }
    public void setTimerDurationSeconds(int timerDurationSeconds) { this.timerDurationSeconds = timerDurationSeconds; }
    public int getTimerNotifyIntervalSeconds() { return timerNotifyIntervalSeconds; }
    public void setTimerNotifyIntervalSeconds(int timerNotifyIntervalSeconds) { this.timerNotifyIntervalSeconds = timerNotifyIntervalSeconds; }
    public boolean isTimerRunning() { return timerRunning; }
    public void setTimerRunning(boolean timerRunning) { this.timerRunning = timerRunning; }
    public Instant getTimerStartedAt() { return timerStartedAt; }
    public void setTimerStartedAt(Instant timerStartedAt) { this.timerStartedAt = timerStartedAt; }
    public int getTimerElapsedSeconds() { return timerElapsedSeconds; }
    public void setTimerElapsedSeconds(int timerElapsedSeconds) { this.timerElapsedSeconds = timerElapsedSeconds; }
}
