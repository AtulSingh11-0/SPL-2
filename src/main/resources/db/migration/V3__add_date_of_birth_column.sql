-- Flyway V3: add date_of_birth column to players table (string)

ALTER TABLE players
  ADD COLUMN IF NOT EXISTS date_of_birth VARCHAR(100) NULL;

