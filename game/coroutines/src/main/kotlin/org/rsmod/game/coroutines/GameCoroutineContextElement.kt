package org.rsmod.game.coroutines

import org.rsmod.game.coroutines.state.GameCoroutineDeferValueState
import org.rsmod.game.coroutines.state.GameCoroutinePredicateState
import org.rsmod.game.coroutines.state.GameCoroutineState
import org.rsmod.game.coroutines.state.GameCoroutineTimedState
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.reflect.KClass

public class GameCoroutineContextElement : AbstractCoroutineContextElement(CoroutineContextKey) {

    private var coroutine: GameCoroutine<out Any>? = null

    public val isSuspended: Boolean
        get() = coroutine != null

    public val isIdle: Boolean
        get() = coroutine == null

    public fun tryResume() {
        val coroutine = coroutine ?: return
        val resume = coroutine.resume()
        if (resume && this.coroutine === coroutine) {
            this.coroutine = null
        }
    }

    public fun cancel(): Nothing {
        coroutine = null
        throw CancellationException()
    }

    public fun pause(ticks: Int, continuation: Continuation<Unit>) {
        val state = GameCoroutineTimedState(ticks)
        coroutine = GameCoroutine(continuation, state)
    }

    public fun pause(condition: () -> Boolean, continuation: Continuation<Unit>) {
        val state = GameCoroutinePredicateState(condition)
        coroutine = GameCoroutine(continuation, state)
    }

    public fun <T : Any> pause(type: KClass<T>, continuation: Continuation<T>) {
        val state = GameCoroutineDeferValueState(type)
        coroutine = GameCoroutine(continuation, state)
    }

    @Suppress("UNCHECKED_CAST")
    public fun submit(value: Any) {
        val coroutine = coroutine as? GameCoroutine<Any> ?: error("${toString()} is not suspended.")
        val state = coroutine.state
        if (state !is GameCoroutineDeferValueState || state.type != value::class) {
            return
        }
        state.set(value)
        tryResume()
    }

    private fun <T> GameCoroutine<T>.resume(): Boolean {
        val deferred = state.resumeOrNull() ?: return false
        continuation.resume(deferred)
        return true
    }

    override fun toString(): String = "GameCoroutineContextElement"

    internal companion object {

        internal object CoroutineContextKey : CoroutineContext.Key<GameCoroutineContextElement>

        private data class GameCoroutine<T>(
            val continuation: Continuation<T>,
            val state: GameCoroutineState<T>
        )
    }
}
