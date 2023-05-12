package org.rsmod.game.coroutines

import org.rsmod.game.coroutines.complete.GameCoroutineSimpleCompletion
import org.rsmod.game.coroutines.throwable.ScopeCancellationException
import kotlin.coroutines.Continuation
import kotlin.coroutines.startCoroutine

public class GameCoroutineScope {

    private val _children = mutableListOf<GameCoroutine>()
    public val children: List<GameCoroutine> get() = _children

    public fun launch(
        coroutine: GameCoroutine = GameCoroutine(),
        completion: Continuation<Unit> = GameCoroutineSimpleCompletion,
        block: suspend GameCoroutine.() -> Unit
    ): GameCoroutine {
        block.startCoroutine(coroutine, completion)
        if (coroutine.isSuspended) _children += coroutine
        return coroutine
    }

    public fun advance() {
        _children.forEach { it.resume() }
        _children.removeIf { it.isIdle }
    }

    public fun cancel() {
        _children.forEach { it.cancel(ScopeCancellationException) }
        _children.clear()
    }
}
