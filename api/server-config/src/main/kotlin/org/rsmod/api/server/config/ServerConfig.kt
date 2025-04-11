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

public data class WorldConfig(val realm: Realm, val worldId: Int)

public data class GameConfig(val xpRate: Int, val spawn: CoordGrid, val respawn: CoordGrid)

/**
 * @param firstLaunch A flag that is enabled when a server config file is not found and a default
 *   one is created.
 */
public data class MetaConfig(val firstLaunch: Boolean)
