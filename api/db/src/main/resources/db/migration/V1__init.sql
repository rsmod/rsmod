CREATE TABLE realms (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    description TEXT
);

INSERT INTO realms (id, name, description)
VALUES
    (1, 'dev', 'Development environment'),
    (2, 'main', 'Production environment');

CREATE TABLE worlds (
    id INTEGER PRIMARY KEY,
    activity TEXT,
    flags INTEGER DEFAULT 0,
    description TEXT
);

INSERT INTO worlds (id) VALUES (1);

CREATE TABLE accounts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    login_username TEXT NOT NULL UNIQUE,
    display_name TEXT UNIQUE,
    hashed_password TEXT NOT NULL,
    email TEXT UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE characters (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    account_id INTEGER NOT NULL,
    realm_id INTEGER NOT NULL,
    world_id INTEGER,
    x INTEGER NOT NULL,
    z INTEGER NOT NULL,
    level INTEGER NOT NULL,
    varps TEXT NOT NULL DEFAULT '{}',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    last_logout TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
    FOREIGN KEY (realm_id) REFERENCES realms(id),
    FOREIGN KEY (world_id) REFERENCES worlds(id)
);

-- Index to optimize character lookups by `account_id` and `realm_id` (our primary access pattern).
CREATE INDEX idx_characters_account_realm ON characters(account_id, realm_id);

CREATE TABLE stats (
    character_id INTEGER NOT NULL,
    stat_id INTEGER NOT NULL,
    vislevel INTEGER NOT NULL,
    baselevel INTEGER NOT NULL,
    xp INTEGER NOT NULL,
    updated_at TIMESTAMP,
    FOREIGN KEY (character_id) REFERENCES characters(id) ON DELETE CASCADE,
    UNIQUE (character_id, stat_id)
);

-- Index to optimize queries that filter `stats` by `character_id` (our main use case).
CREATE INDEX idx_stats_character_id ON stats(character_id);

CREATE TABLE inventories (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    character_id INTEGER NOT NULL,
    inv_id INTEGER NOT NULL,
    FOREIGN KEY (character_id) REFERENCES characters(id) ON DELETE CASCADE,
    UNIQUE (character_id, inv_id)
);

-- Index to optimize queries that filter `inventories` by `character_id` (our main use case).
CREATE INDEX idx_inventories_character_id ON inventories(character_id);

CREATE TABLE inventory_objs (
    inventory_id INTEGER NOT NULL,
    slot INTEGER NOT NULL,
    obj INTEGER NOT NULL,
    count INTEGER NOT NULL,
    PRIMARY KEY (inventory_id, slot),
    FOREIGN KEY (inventory_id) REFERENCES inventories(id) ON DELETE CASCADE
);
