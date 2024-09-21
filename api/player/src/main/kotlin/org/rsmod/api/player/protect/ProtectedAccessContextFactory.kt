package org.rsmod.api.player.protect

import jakarta.inject.Inject
import org.rsmod.events.EventBus
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.pathfinder.collision.CollisionFlagMap

public class ProtectedAccessContextFactory
@Inject
constructor(
    private val collision: CollisionFlagMap,
    private val eventBus: EventBus,
    private val objTypes: ObjTypeList,
) {
    public fun create(): ProtectedAccessContext =
        ProtectedAccessContext(
            getEventBus = { eventBus },
            getCollision = { collision },
            getObjTypes = { objTypes },
        )

    public fun create(
        eventBus: EventBus = this.eventBus,
        collision: CollisionFlagMap = this.collision,
        objTypes: ObjTypeList = this.objTypes,
    ): ProtectedAccessContext =
        ProtectedAccessContext(
            getEventBus = { eventBus },
            getCollision = { collision },
            getObjTypes = { objTypes },
        )
}
