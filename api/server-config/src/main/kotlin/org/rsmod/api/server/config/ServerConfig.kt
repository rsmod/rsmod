package org.rsmod.api.server.config

import org.rsmod.api.realm.Realm
import org.rsmod.map.CoordGrid

public data class ServerConfig(
    val name: String,
    val world: WorldConfig,
    val game: GameConfig,
    val meta: MetaConfig,
) {
    override fun toString(): String =
        "ServerConfig(" +
            "name='$name', " +
            "realm='${world.realm.dbName}', " +
            "world=${world.worldId}, " +
            "xpRate=${game.xpRate}, " +
            "spawn=${game.spawn}" +
            ")"
}

/**
 * @param requireRegistration If `true`, players must have a pre-registered account in the database
 *   to log in. If `false`, new accounts will be automatically created on first login.
 * @param ignorePasswords If `true`, any password will be accepted for any account without
 *   verification. Note: This flag may only take effect in certain environments (e.g., when [realm]
 *   is [Realm.Dev]), depending on the login server's safeguards.
 * @param autoAssignDisplayName If `true`, player display names will be automatically assigned based
 *   on their login name.
 */
public data class WorldConfig(
    val realm: Realm,
    val worldId: Int,
    val requireRegistration: Boolean,
    val ignorePasswords: Boolean,
    val autoAssignDisplayName: Boolean,
)

public data class GameConfig(val xpRate: Int, val spawn: CoordGrid, val respawn: CoordGrid)

/**
 * @param firstLaunch A flag that is enabled when a server config file is not found and a default
 *   one is created.
 */
public data class MetaConfig(val firstLaunch: Boolean)

// Declared as an extension to avoid `JsonIgnore` annotations.
public val WorldConfig.isDevRealm: Boolean
    get() = realm.isDev
