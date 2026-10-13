-- OSRS experience rates: reset the "dev" realm's default 150x player xp rate to 1x
-- (stored as 100 in scaled format), including characters created with the old default.
UPDATE realms
SET
    player_xp_rate_in_hundreds = 100
WHERE name = 'dev';

UPDATE characters
SET
    xp_rate_in_hundreds = 100
WHERE xp_rate_in_hundreds = 15000
    AND realm_id = (SELECT id FROM realms WHERE name = 'dev');
