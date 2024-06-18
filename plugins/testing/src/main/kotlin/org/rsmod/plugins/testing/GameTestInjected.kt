package org.rsmod.plugins.testing

import org.rsmod.plugins.api.cache.map.xtea.XteaRepository
import org.rsmod.plugins.api.map.GameMap
import org.rsmod.plugins.api.pathfinder.BoundValidator
import org.rsmod.plugins.api.pathfinder.PathValidator
import org.rsmod.plugins.api.pathfinder.RayCastFactory
import org.rsmod.plugins.api.pathfinder.RouteFactory
import org.rsmod.plugins.api.pathfinder.StepFactory
import com.google.inject.Inject

internal data class GameTestInjected @Inject constructor(
    internal val gameMap: GameMap,
    internal val routeFactory: RouteFactory,
    internal val rayCastFactory: RayCastFactory,
    internal val stepFactory: StepFactory,
    internal val pathValidator: PathValidator,
    internal val boundValidator: BoundValidator,
    internal val xteaRepository: XteaRepository
)
