package org.rsmod.api.player.protect

import org.rsmod.events.EventBus
import org.rsmod.pathfinder.collision.CollisionFlagMap

public data class ProtectedAccessContext(
    private val getEventBus: () -> EventBus,
    private val getCollision: () -> CollisionFlagMap,
) {
    public val eventBus: EventBus by lazy(LazyThreadSafetyMode.NONE) { getEventBus() }
    public val collision: CollisionFlagMap by lazy(LazyThreadSafetyMode.NONE) { getCollision() }

    public companion object {
        /**
         * A lightweight, empty context for use when a full [ProtectedAccessContext] is not
         * required.
         *
         * ## Usage:
         * - Use in cases where the [ProtectedAccessContext] dependencies are not needed within the
         *   scope.
         * - Accessing any context-dependent properties will throw an [IllegalStateException].
         *
         * _Excessive use of [EMPTY_CTX] may indicate improper usage of [ProtectedAccessContext]._
         */
        public val EMPTY_CTX: ProtectedAccessContext =
            ProtectedAccessContext(
                getEventBus = { throw IllegalStateException("No event bus provided.") },
                getCollision = { throw IllegalStateException("No collision map provided.") },
            )
    }
}
