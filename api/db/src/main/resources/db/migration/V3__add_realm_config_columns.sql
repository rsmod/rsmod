ALTER TABLE realms
ADD COLUMN login_message TEXT DEFAULT 'Welcome to RS Mod.';

ALTER TABLE realms
ADD COLUMN xp_rate INTEGER NOT NULL DEFAULT 1;

ALTER TABLE realms
ADD COLUMN spawn_coord TEXT NOT NULL DEFAULT '0_50_50_21_18';

ALTER TABLE realms
ADD COLUMN respawn_coord TEXT NOT NULL DEFAULT '0_50_50_21_18';

ALTER TABLE realms
ADD COLUMN dev_mode BOOLEAN NOT NULL DEFAULT 0;

ALTER TABLE realms
ADD COLUMN require_registration BOOLEAN NOT NULL DEFAULT 1;

ALTER TABLE realms
ADD COLUMN ignore_passwords BOOLEAN NOT NULL DEFAULT 0;

ALTER TABLE realms
ADD COLUMN auto_assign_display_names BOOLEAN NOT NULL DEFAULT 0;

UPDATE realms
SET
    xp_rate = 100,
    dev_mode = 1,
    require_registration = 0,
    ignore_passwords = 1,
    auto_assign_display_names = 1
WHERE name = 'dev';
