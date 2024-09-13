package org.rsmod.api.player.protect

import org.rsmod.events.EventBus
import org.rsmod.pathfinder.collision.CollisionFlagMap

/**
 * Provides contextual access to external systems required by the [ProtectedAccess] scope.
 *
 * This class allows the implicit passing of certain dependencies (e.g., [EventBus]) to functions
 * within the [ProtectedAccess] scope, reducing the burden on developers to inject or explicitly
 * pass these dependencies in every instance where they are required.
 *
 * ## Design Decision:
 * The [ProtectedAccessContext] class was introduced to balance ease of use and flexibility in the
 * framework. While explicitly passing dependencies (e.g., through function parameters) is often
 * considered best practice, this can become cumbersome when frequently interacting with multiple
 * systems, such as ui-related functions and movement (more specifically, teleporting). The context
 * provides these dependencies by default, but still allows consumers to override them if necessary.
 *
 * ### Why Lazy Initialization?
 * Context dependencies are initialized lazily. This ensures that they are only instantiated when
 * needed, improving performance in cases where certain systems are not required during the lifetime
 * of a particular interaction.
 *
 * **Note**: The lazy initialization assumes single-threaded usage, as [LazyThreadSafetyMode.NONE]
 * is used. If you're extending the context to work in a multithreaded environment, you may need to
 * adapt this behavior accordingly.
 *
 * ## Usage:
 * In most cases, this class will be used implicitly in the [ProtectedAccess] scope. Developers
 * should generally not need to interact with the context directly, as it provides sensible defaults
 * for common use cases. However, for specialized behavior (e.g., testing, custom logic), developers
 * can override the default context by passing their own dependencies.
 *
 * ### Extensibility:
 * The context was designed with flexibility in mind, allowing additional dependencies to be added
 * in the future without affecting existing code.
 *
 * ### Example:
 * ```
 * val customContext = ProtectedAccessContext(
 *     getEventBus = { customEventBus },
 *     getCollision = { customCollisionMap }
 * )
 *
 * player.withProtectedAccess(customContext) { ... }
 * ```
 *
 * Or
 *
 * ```
 * protectedAccess.telejump(dest, collision = customCollisionMap)
 * ```
 */
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
