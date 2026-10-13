package org.runerealm.api.testing

import jakarta.inject.Inject
import org.runerealm.api.game.process.GameCycle
import org.runerealm.api.route.BoundValidator
import org.runerealm.api.route.RayCastFactory
import org.runerealm.api.route.RayCastValidator
import org.runerealm.api.route.RouteFactory
import org.runerealm.api.route.StepFactory
import org.runerealm.events.EventBus
import org.runerealm.game.map.xtea.XteaMap
import org.runerealm.game.type.TypeListMap
import org.runerealm.routefinder.collision.CollisionFlagMap

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
