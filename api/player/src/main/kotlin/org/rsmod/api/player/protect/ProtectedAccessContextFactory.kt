package org.rsmod.api.player.protect

import jakarta.inject.Inject
import org.rsmod.api.player.interact.LocInteractions
import org.rsmod.api.random.GameRandom
import org.rsmod.events.EventBus
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.routefinder.collision.CollisionFlagMap

public class ProtectedAccessContextFactory
@Inject
constructor(
    private val random: GameRandom,
    private val collision: CollisionFlagMap,
    private val eventBus: EventBus,
    private val objTypes: ObjTypeList,
    private val locInteractions: LocInteractions,
) {
    public fun create(): ProtectedAccessContext =
        ProtectedAccessContext(
            getRandom = { random },
            getEventBus = { eventBus },
            getCollision = { collision },
            getObjTypes = { objTypes },
            getLocInteractions = { locInteractions },
        )
}
