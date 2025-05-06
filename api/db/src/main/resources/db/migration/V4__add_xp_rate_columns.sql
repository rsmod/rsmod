ALTER TABLE realms
DROP COLUMN xp_rate;

ALTER TABLE realms
ADD COLUMN player_xp_rate_in_hundreds INTEGER NOT NULL DEFAULT 100;

ALTER TABLE realms
ADD COLUMN global_xp_rate_in_hundreds INTEGER NOT NULL DEFAULT 100;

ALTER TABLE characters
ADD COLUMN xp_rate_in_hundreds INTEGER NOT NULL DEFAULT 100;

-- Apply a default 150x base xp rate to the "dev" realm (stored as 15,000 in scaled format).
UPDATE realms
SET
    player_xp_rate_in_hundreds = 15000
WHERE name = 'dev';
