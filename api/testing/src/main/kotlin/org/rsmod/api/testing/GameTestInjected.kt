package org.rsmod.api.testing

import jakarta.inject.Inject
import org.rsmod.api.game.process.GameCycle
import org.rsmod.api.route.BoundValidator
import org.rsmod.api.route.RayCastFactory
import org.rsmod.api.route.RayCastValidator
import org.rsmod.api.route.RouteFactory
import org.rsmod.api.route.StepFactory
import org.rsmod.events.EventBus
import org.rsmod.game.map.xtea.XteaMap
import org.rsmod.game.type.TypeListMap
import org.rsmod.routefinder.collision.CollisionFlagMap

internal data class GameTestInjected
@Inject
constructor(
    internal val gameCycle: GameCycle,
    internal val collision: CollisionFlagMap,
    internal val routeFactory: RouteFactory,
    internal val rayCastFactory: RayCastFactory,
    internal val stepFactory: StepFactory,
    internal val rayCastValidator: RayCastValidator,
    internal val boundValidator: BoundValidator,
    internal val xteaRepository: XteaMap,
    internal val cacheTypes: TypeListMap,
    internal val eventBus: EventBus,
)
