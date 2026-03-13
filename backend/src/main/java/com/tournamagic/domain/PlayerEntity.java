package com.tournamagic.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "players")
public class PlayerEntity {
    @Id
    private String id;

    @Column(name = "tournament_id", nullable = false)
    private String tournamentId;

    @Column(nullable = false)
    private String name;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTournamentId() { return tournamentId; }
    public void setTournamentId(String tournamentId) { this.tournamentId = tournamentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
