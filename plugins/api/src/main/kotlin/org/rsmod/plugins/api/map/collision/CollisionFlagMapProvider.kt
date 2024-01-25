package org.rsmod.plugins.api.map.collision

import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.plugins.api.map.GameMap
import jakarta.inject.Inject
import jakarta.inject.Provider

public class CollisionFlagMapProvider @Inject constructor(
    private val map: GameMap
) : Provider<CollisionFlagMap> {

    override fun get(): CollisionFlagMap {
        return map.flags
    }
}
