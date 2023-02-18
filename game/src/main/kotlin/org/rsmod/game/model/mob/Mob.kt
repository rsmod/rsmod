package org.rsmod.game.model.mob

import org.rsmod.game.coroutines.GameCoroutine
import org.rsmod.game.coroutines.GameCoroutineScope
import org.rsmod.game.model.client.Entity
import org.rsmod.game.model.map.Coordinates

public sealed class Mob(
    public val coroutineScope: GameCoroutineScope = GameCoroutineScope()
) {

    public abstract val entity: Entity

    private var activeCoroutine: GameCoroutine? = null

    public var index: Int
        get() = entity.index
        set(value) { entity.index = value }

    public var coords: Coordinates
        get() = entity.coords
        set(value) { entity.coords = value }

    public var prevCoords: Coordinates
        get() = entity.prevCoords
        set(value) { entity.prevCoords = value }

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
}
