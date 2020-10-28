package gg.rsmod.game.coroutine

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
    private val continuation: Continuation<T>,
    private val condition: GameCoroutineCondition<T>
) {

    fun resume(): Boolean {
        val value = condition.get() ?: return false
        continuation.resume(value)
        return true
    }

    fun submit(value: T) {
        if (condition !is GameCoroutineDeferredCondition) return
        if (condition.type == value::class) {
            condition.value = value
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
        val condition = GameCoroutineTickCondition(ticks)
        coroutine = GameCoroutine(continuation, condition)
    }

    fun delay(predicate: () -> Boolean, continuation: Continuation<Unit>) {
        val condition = GameCoroutinePredicateCondition(predicate)
        coroutine = GameCoroutine(continuation, condition)
    }

    fun <T : Event> delay(eventType: KClass<T>, continuation: Continuation<T>) {
        val condition = GameCoroutineDeferredCondition(eventType)
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
            error.printStackTrace()
        }
    }
}

interface GameCoroutineCondition<T> {

    fun get(): T?
}

class GameCoroutineTickCondition(private var ticks: Int) : GameCoroutineCondition<Unit> {

    override fun get(): Unit? {
        return if (--ticks == 0) Unit else null
    }
}

class GameCoroutinePredicateCondition(private val predicate: () -> Boolean) : GameCoroutineCondition<Unit> {

    override fun get(): Unit? {
        return if (predicate()) Unit else null
    }
}

class GameCoroutineDeferredCondition<T : Any>(val type: KClass<T>, var value: T? = null) : GameCoroutineCondition<T> {

    override fun get(): T? = value
}
