package org.rsmod.game.coroutines

import org.rsmod.game.coroutines.complete.GameCoroutineSimpleCompletion
import org.rsmod.game.coroutines.complete.GameCoroutineSupervisedCompletion
import kotlin.coroutines.Continuation
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.startCoroutine

@Suppress("MemberVisibilityCanBePrivate")
public class GameCoroutineScope(
    public var superviseCoroutines: Boolean = false
) : AutoCloseable {

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
        result.exceptionOrNull()?.let { if (it !is CancellationException) throw it }
        children -= coroutine
    }

    private fun GameCoroutine.completion(): Continuation<Unit> = if (superviseCoroutines) {
        GameCoroutineSupervisedCompletion(this@GameCoroutineScope, this)
    } else {
        GameCoroutineSimpleCompletion
    }

    public fun getSupervisedChildren(): List<GameCoroutine> = children

    override fun close() {
        val iterator = children.toList()
        iterator.forEach { it.cancel() }
    }
}
