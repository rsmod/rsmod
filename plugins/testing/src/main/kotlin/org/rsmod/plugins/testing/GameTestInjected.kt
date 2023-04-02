package org.rsmod.plugins.testing

import org.rsmod.plugins.api.map.GameMap
import org.rsmod.plugins.api.pathfinder.PathValidator
import org.rsmod.plugins.api.pathfinder.RouteFactory
import org.rsmod.plugins.api.pathfinder.StepFactory
import javax.inject.Inject

internal data class GameTestInjected @Inject constructor(
    internal val gameMap: GameMap,
    internal val pathValidator: PathValidator,
    internal val routeFactory: RouteFactory,
    internal val stepFactory: StepFactory
)
