package org.rsmod.plugins.api.map.collision

import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.plugins.api.map.GameMap
import javax.inject.Inject
import javax.inject.Provider

public class CollisionFlagMapProvider @Inject constructor(
    private val map: GameMap
) : Provider<CollisionFlagMap> {

    override fun get(): CollisionFlagMap {
        return map.flags
    }
}
