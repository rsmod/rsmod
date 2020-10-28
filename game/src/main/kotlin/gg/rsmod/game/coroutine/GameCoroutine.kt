package gg.rsmod.game.coroutine

import com.github.michaelbull.logging.InlineLogger
import gg.rsmod.game.event.Event
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.CancellationException
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.coroutines.startCoroutine
import kotlin.reflect.KClass

private val logger = InlineLogger()

internal val CoroutineContext.task: GameCoroutineTask
    get() = get(GameCoroutineTask) ?: GameCoroutineTask()

suspend fun delay(ticks: Int = 1) {
    if (ticks <= 0) return
    return suspendCoroutineUninterceptedOrReturn {
        it.context.task.delay(ticks, it)
        COROUTINE_SUSPENDED
    }
}

suspend fun delay(predicate: () -> Boolean) {
    if (predicate()) return
    return suspendCoroutineUninterceptedOrReturn {
        it.context.task.delay(predicate, it)
        COROUTINE_SUSPENDED
    }
}

suspend fun <T : Event> delay(eventType: KClass<T>): T {
    return suspendCoroutineUninterceptedOrReturn {
        it.context.task.delay(eventType, it)
        COROUTINE_SUSPENDED
    }
}

suspend fun cancel(): Nothing = suspendCancellableCoroutine {
    it.context.task.cancel()
    it.cancel()
}

class GameCoroutine<T : Any>(
    private val cont: Continuation<T>,
    private val state: GameCoroutineState<T>
) {

    fun resume(): Boolean {
        if (!state.resume()) {
            return false
        }
        val value = state.get()
        cont.resume(value)
        return true
    }

    fun submit(value: T) {
        if (state !is GameCoroutineDeferValueState) return
        if (state.type == value::class) {
            state.set(value)
        }
    }
}

class GameCoroutineTask(
    private var coroutine: GameCoroutine<out Any>? = null
) : AbstractCoroutineContextElement(GameCoroutineTask) {

    companion object Key : CoroutineContext.Key<GameCoroutineTask>

    val idle: Boolean
        get() = coroutine == null

    fun delay(ticks: Int, continuation: Continuation<Unit>) {
        val condition = GameCoroutineTimedState(ticks)
        coroutine = GameCoroutine(continuation, condition)
    }

    fun delay(predicate: () -> Boolean, continuation: Continuation<Unit>) {
        val condition = GameCoroutinePredicateState(predicate)
        coroutine = GameCoroutine(continuation, condition)
    }

    fun <T : Event> delay(eventType: KClass<T>, continuation: Continuation<T>) {
        val condition = GameCoroutineDeferValueState(eventType)
        coroutine = GameCoroutine(continuation, condition)
    }

    fun cycle() {
        val coroutine = coroutine ?: return
        val resume = coroutine.resume()
        if (resume && this.coroutine === coroutine) {
            this.coroutine = null
        }
    }

    fun cancel() {
        coroutine = null
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> submit(event: T) {
        val coroutine = coroutine as? GameCoroutine<Any> ?: return
        coroutine.submit(event)
    }

    fun launch(block: suspend () -> Unit) {
        block.startCoroutine(DefaultGameCoroutineContinuation)
    }
}

object DefaultGameCoroutineContinuation : Continuation<Unit> {

    override val context: CoroutineContext = EmptyCoroutineContext

    override fun resumeWith(result: Result<Unit>) {
        val error = result.exceptionOrNull()
        if (error != null && error !is CancellationException) {
            logger.error(error) {}
        }
    }
}

interface GameCoroutineState<T> {

    fun resume(): Boolean

    fun get(): T
}

class GameCoroutineTimedState(private var ticks: Int) : GameCoroutineState<Unit> {

    override fun resume(): Boolean {
        return --ticks == 0
    }

    override fun get() {}
}

class GameCoroutinePredicateState(private val predicate: () -> Boolean) : GameCoroutineState<Unit> {

    override fun resume(): Boolean {
        return predicate()
    }

    override fun get() {}
}

class GameCoroutineDeferValueState<T : Any>(
    val type: KClass<T>,
    var resume: Boolean = false
) : GameCoroutineState<T> {

    private lateinit var value: T

    fun set(value: T) {
        this.value = value
        this.resume = true
    }

    override fun resume(): Boolean {
        return resume
    }

    override fun get(): T = value
}
