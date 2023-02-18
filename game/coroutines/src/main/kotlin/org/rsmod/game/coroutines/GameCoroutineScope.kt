package org.rsmod.game.coroutines

import org.rsmod.game.coroutines.complete.GameCoroutineSimpleCompletion
import org.rsmod.game.coroutines.throwable.ScopeCancellationException
import kotlin.coroutines.Continuation
import kotlin.coroutines.startCoroutine

@Suppress("MemberVisibilityCanBePrivate")
public class GameCoroutineScope {

    private val children = mutableListOf<GameCoroutine>()

    public fun launch(
        coroutine: GameCoroutine = GameCoroutine(),
        completion: Continuation<Unit> = GameCoroutineSimpleCompletion,
        block: suspend (GameCoroutine).() -> Unit
    ): GameCoroutine {
        block.startCoroutine(coroutine, completion)
        if (coroutine.isSuspended) {
            children += coroutine
        }
        return coroutine
    }

    public fun advance() {
        children.forEach { it.resume() }
        children.removeIf { it.isIdle }
    }

    public fun cancel() {
        children.forEach { it.cancel(ScopeCancellationException) }
        children.clear()
    }

    public fun getChildren(): List<GameCoroutine> = children
}
