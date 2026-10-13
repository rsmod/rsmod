-- Rebrand: replace the old default login message.
UPDATE realms
SET
    login_message = 'Welcome to RuneRealm.'
WHERE login_message = 'Welcome to RS Mod.';
