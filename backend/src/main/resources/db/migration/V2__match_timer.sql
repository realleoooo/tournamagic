ALTER TABLE matches ADD COLUMN timer_direction VARCHAR(8) NOT NULL DEFAULT 'up';
ALTER TABLE matches ADD COLUMN timer_duration_seconds INT NOT NULL DEFAULT 0;
ALTER TABLE matches ADD COLUMN timer_notify_interval_seconds INT NOT NULL DEFAULT 600;
ALTER TABLE matches ADD COLUMN timer_running BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE matches ADD COLUMN timer_started_at TIMESTAMP;
ALTER TABLE matches ADD COLUMN timer_elapsed_seconds INT NOT NULL DEFAULT 0;
