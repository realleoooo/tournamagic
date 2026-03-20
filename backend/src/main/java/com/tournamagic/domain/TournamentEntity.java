package com.tournamagic.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "tournaments")
public class TournamentEntity {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "join_code", nullable = false)
    private String joinCode;

    @Column(name = "join_enabled", nullable = false)
    private boolean joinEnabled;

    @Column(name = "join_code_expires_at")
    private Instant joinCodeExpiresAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public String getJoinCode() { return joinCode; }
    public void setJoinCode(String joinCode) { this.joinCode = joinCode; }
    public boolean isJoinEnabled() { return joinEnabled; }
    public void setJoinEnabled(boolean joinEnabled) { this.joinEnabled = joinEnabled; }
    public Instant getJoinCodeExpiresAt() { return joinCodeExpiresAt; }
    public void setJoinCodeExpiresAt(Instant joinCodeExpiresAt) { this.joinCodeExpiresAt = joinCodeExpiresAt; }
}
