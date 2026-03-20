ALTER TABLE players ADD COLUMN IF NOT EXISTS claimed_by_email VARCHAR(255);
ALTER TABLE players ADD COLUMN IF NOT EXISTS claimed_by_name VARCHAR(255);

ALTER TABLE tournament_participants ADD COLUMN IF NOT EXISTS player_id VARCHAR(36);

CREATE UNIQUE INDEX IF NOT EXISTS idx_participants_player_id ON tournament_participants (player_id);
