package org.rsmod.api.player.protect

import jakarta.inject.Inject
import org.rsmod.events.EventBus
import org.rsmod.pathfinder.collision.CollisionFlagMap

public class ProtectedAccessContextFactory
@Inject
constructor(private val collision: CollisionFlagMap, private val eventBus: EventBus) {
    public fun create(): ProtectedAccessContext =
        ProtectedAccessContext(getEventBus = { eventBus }, getCollision = { collision })
}
