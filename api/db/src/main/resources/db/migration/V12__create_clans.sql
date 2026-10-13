-- Clans ("Your Clan" tab). Ids are assigned by the game server, which keeps every clan in memory.
CREATE TABLE clans (
    id INTEGER PRIMARY KEY,
    realm_id INTEGER NOT NULL,
    name TEXT NOT NULL COLLATE NOCASE,
    -- Epoch minute the clan was formed (what the client shows as its creation date).
    created_minute INTEGER NOT NULL,
    -- Minimum clan ranks needed to talk in and kick from the clan channel.
    talk_rank INTEGER NOT NULL DEFAULT 0,
    kick_rank INTEGER NOT NULL DEFAULT 100,
    FOREIGN KEY (realm_id) REFERENCES realms(id),
    UNIQUE (realm_id, name)
);

-- A character can be a ranked member of one clan only.
CREATE TABLE clan_members (
    clan_id INTEGER NOT NULL,
    character_id INTEGER NOT NULL UNIQUE,
    display_name TEXT NOT NULL,
    rank INTEGER NOT NULL,
    -- RuneScape day (days since 27 February 2002) the member joined.
    join_rune_day INTEGER NOT NULL,
    FOREIGN KEY (clan_id) REFERENCES clans(id) ON DELETE CASCADE,
    FOREIGN KEY (character_id) REFERENCES characters(id) ON DELETE CASCADE
);

CREATE INDEX idx_clan_members_clan_id ON clan_members(clan_id);
