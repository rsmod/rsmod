package org.rsmod.coroutine

import kotlin.coroutines.Continuation
import kotlin.coroutines.startCoroutine
import kotlinx.coroutines.CancellationException
import org.rsmod.coroutine.suspension.GameCoroutineSimpleCompletion

class TestCoroutineScope {
    private val _children: MutableList<GameCoroutine> = mutableListOf()
    val children: List<GameCoroutine>
        get() = _children

    fun launch(
        coroutine: GameCoroutine = GameCoroutine(),
        completion: Continuation<Unit> = GameCoroutineSimpleCompletion,
        block: suspend GameCoroutine.() -> Unit,
    ): GameCoroutine {
        block.startCoroutine(coroutine, completion)
        if (coroutine.isSuspended) {
            _children += coroutine
        }
        return coroutine
    }

    fun advance() {
        _children.forEach(GameCoroutine::advance)
        _children.removeIf(GameCoroutine::isIdle)
    }

    fun cancel() {
        _children.forEach { it.cancel(ScopeCancellationException) }
        _children.clear()
    }

    object ScopeCancellationException : CancellationException() {
        private fun readResolve(): Any = ScopeCancellationException
    }
}
