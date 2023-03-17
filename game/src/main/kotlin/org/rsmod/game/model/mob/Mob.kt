package org.rsmod.game.model.mob

import org.rsmod.game.coroutines.GameCoroutine
import org.rsmod.game.coroutines.GameCoroutineScope
import org.rsmod.game.map.Coordinates
import org.rsmod.game.model.client.Entity
import org.rsmod.game.model.mob.move.MovementQueue

public sealed class Mob(
    public var index: Int = INVALID_INDEX,
    public val coroutineScope: GameCoroutineScope = GameCoroutineScope(),
    public val movement: MovementQueue = MovementQueue()
) {

    public abstract val entity: Entity

    public var coords: Coordinates
        get() = entity.coords
        set(value) { entity.coords = value }

    public var activeCoroutine: GameCoroutine? = null
        private set

    public var prevCoords: Coordinates = Coordinates.ZERO

    public fun launchCoroutine(block: suspend (GameCoroutine).() -> Unit): GameCoroutine {
        return coroutineScope.launch(block = block)
    }

    public fun launchStrictCoroutine(block: suspend GameCoroutine.() -> Unit): GameCoroutine {
        activeCoroutine?.cancel()
        val coroutine = coroutineScope.launch(block = block)
        if (coroutine.isSuspended) {
            activeCoroutine = coroutine
        }
        return coroutine
    }

    /**
     * This method is responsible for cleaning up any ongoing tasks
     * that the mob may be responsible for. This includes things such
     * as coroutines created by [coroutineScope]. If these coroutines
     * are not cancelled properly they may linger in memory and run
     * indefinitely.
     */
    public fun finalize() {
        coroutineScope.cancel()
    }

    public companion object {

        public const val INVALID_INDEX: Int = -1
    }
}
