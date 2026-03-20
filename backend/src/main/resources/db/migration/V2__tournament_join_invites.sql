ALTER TABLE tournaments ADD COLUMN IF NOT EXISTS join_code VARCHAR(16);
ALTER TABLE tournaments ADD COLUMN IF NOT EXISTS join_enabled BOOLEAN;
ALTER TABLE tournaments ADD COLUMN IF NOT EXISTS join_code_expires_at TIMESTAMP NULL;

UPDATE tournaments
SET join_code = UPPER(SUBSTRING(REPLACE(id, '-', ''), 1, 12)),
    join_enabled = TRUE
WHERE join_code IS NULL OR TRIM(join_code) = '' OR join_enabled IS NULL;

ALTER TABLE tournaments ALTER COLUMN join_code SET NOT NULL;
ALTER TABLE tournaments ALTER COLUMN join_enabled SET NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS idx_tournaments_join_code ON tournaments (join_code);

CREATE TABLE IF NOT EXISTS tournament_participants (
    id VARCHAR(36) PRIMARY KEY,
    tournament_id VARCHAR(36) NOT NULL,
    user_email VARCHAR(255) NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    joined_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_participants_tournament FOREIGN KEY (tournament_id) REFERENCES tournaments(id),
    CONSTRAINT uq_participants_tournament_user UNIQUE (tournament_id, user_email)
);
