CREATE TABLE tournaments (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE players (
    id VARCHAR(36) PRIMARY KEY,
    tournament_id VARCHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT fk_players_tournament FOREIGN KEY (tournament_id) REFERENCES tournaments(id)
);

CREATE TABLE matches (
    id VARCHAR(36) PRIMARY KEY,
    tournament_id VARCHAR(36) NOT NULL,
    player_a_id VARCHAR(36) NOT NULL,
    player_b_id VARCHAR(36) NOT NULL,
    status VARCHAR(32) NOT NULL,
    wins_a INT NOT NULL,
    wins_b INT NOT NULL,
    winner_id VARCHAR(36),
    CONSTRAINT fk_matches_tournament FOREIGN KEY (tournament_id) REFERENCES tournaments(id)
);
