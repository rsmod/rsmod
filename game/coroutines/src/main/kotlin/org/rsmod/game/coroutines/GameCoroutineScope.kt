package org.rsmod.game.coroutines

import org.rsmod.game.coroutines.complete.GameCoroutineSimpleCompletion
import org.rsmod.game.coroutines.complete.GameCoroutineSupervisedCompletion
import org.rsmod.game.coroutines.throwable.ScopeCancellationException
import kotlin.coroutines.Continuation
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.startCoroutine

@Suppress("MemberVisibilityCanBePrivate")
public class GameCoroutineScope(public var superviseCoroutines: Boolean = false) {

    private val children = mutableListOf<GameCoroutine>()

    public fun launch(
        coroutine: GameCoroutine = GameCoroutine(),
        block: suspend (GameCoroutine).() -> Unit
    ): GameCoroutine {
        val completion = coroutine.completion()
        block.startCoroutine(coroutine, completion)
        if (superviseCoroutines && coroutine.isSuspended) {
            children += coroutine
        }
        return coroutine
    }

    public fun supervisedResume(coroutine: GameCoroutine, result: Result<Unit>) {
        val exception = result.exceptionOrNull()
        /* scope cancellation removes all children at the end */
        if (exception !is ScopeCancellationException) {
            children -= coroutine
        }
        if (exception != null && exception !is CancellationException) {
            throw exception
        }
    }

    private fun GameCoroutine.completion(): Continuation<Unit> = if (superviseCoroutines) {
        GameCoroutineSupervisedCompletion(this@GameCoroutineScope, this)
    } else {
        GameCoroutineSimpleCompletion
    }

    public fun getSupervisedChildren(): List<GameCoroutine> = children

    public fun cancel() {
        children.forEach { it.cancel(ScopeCancellationException()) }
        children.clear()
    }
}
