package org.rsmod.plugins.api.map.collision

import jakarta.inject.Inject
import jakarta.inject.Provider
import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.plugins.api.map.GameMap

public class CollisionFlagMapProvider @Inject constructor(
    private val map: GameMap
) : Provider<CollisionFlagMap> {

    override fun get(): CollisionFlagMap {
        return map.flags
    }
}
