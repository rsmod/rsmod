package org.rsmod.plugins.api.pathfinder

import org.rsmod.game.pathfinder.collision.CollisionStrategies
import org.rsmod.game.pathfinder.collision.CollisionStrategy

public enum class CollisionType(public val strategy: CollisionStrategy) {
    Blocked(CollisionStrategies.Blocked),
    Fly(CollisionStrategies.Fly),
    Indoors(CollisionStrategies.Indoors),
    Normal(CollisionStrategies.Normal),
    Outdoors(CollisionStrategies.Outdoors);
}
