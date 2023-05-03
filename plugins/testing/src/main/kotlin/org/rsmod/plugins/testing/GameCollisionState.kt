package org.rsmod.plugins.testing

import org.rsmod.game.pathfinder.StepValidator
import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.plugins.api.pathfinder.PathValidator
import org.rsmod.plugins.api.pathfinder.RayCastFactory
import org.rsmod.plugins.api.pathfinder.RouteFactory
import org.rsmod.plugins.api.pathfinder.StepFactory

public data class GameCollisionState(
    public val collision: CollisionFlagMap,
    public val routeFactory: RouteFactory,
    public val rayCastFactory: RayCastFactory,
    public val stepFactory: StepFactory,
    public val pathValidator: PathValidator,
    public val stepValidator: StepValidator
)
