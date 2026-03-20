ALTER TABLE tournaments
    ADD COLUMN join_code VARCHAR(16) NOT NULL DEFAULT 'TEMPJOIN',
    ADD COLUMN join_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN join_code_expires_at TIMESTAMP NULL;

CREATE UNIQUE INDEX idx_tournaments_join_code ON tournaments (join_code);

CREATE TABLE tournament_participants (
    id VARCHAR(36) PRIMARY KEY,
    tournament_id VARCHAR(36) NOT NULL,
    user_email VARCHAR(255) NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    joined_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_participants_tournament FOREIGN KEY (tournament_id) REFERENCES tournaments(id),
    CONSTRAINT uq_participants_tournament_user UNIQUE (tournament_id, user_email)
);
