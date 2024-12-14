package org.rsmod.api.player.protect

import jakarta.inject.Inject
import org.rsmod.api.player.interact.LocInteractions
import org.rsmod.events.EventBus
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.pathfinder.collision.CollisionFlagMap

public class ProtectedAccessContextFactory
@Inject
constructor(
    private val collision: CollisionFlagMap,
    private val eventBus: EventBus,
    private val objTypes: ObjTypeList,
    private val locInteractions: LocInteractions,
) {
    public fun create(): ProtectedAccessContext =
        ProtectedAccessContext(
            getEventBus = { eventBus },
            getCollision = { collision },
            getObjTypes = { objTypes },
            getLocInteractions = { locInteractions },
        )

    public fun create(
        eventBus: EventBus = this.eventBus,
        collision: CollisionFlagMap = this.collision,
        objTypes: ObjTypeList = this.objTypes,
        locInteractions: LocInteractions = this.locInteractions,
    ): ProtectedAccessContext =
        ProtectedAccessContext(
            getEventBus = { eventBus },
            getCollision = { collision },
            getObjTypes = { objTypes },
            getLocInteractions = { locInteractions },
        )
}
