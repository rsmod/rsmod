package org.rsmod.coroutine

import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.reflect.KClass
import org.rsmod.coroutine.resume.DeferredResumeCondition
import org.rsmod.coroutine.resume.PredicateResumeCondition
import org.rsmod.coroutine.suspension.GameCoroutineSuspension

public class GameCoroutine(public val debugName: String? = null) {
    public val isIdle: Boolean
        get() = !isSuspended

    public val isSuspended: Boolean
        get() = suspension != null

    private var suspension: GameCoroutineSuspension<Any>? = null

    public fun advance() {
        val suspension = suspension ?: return
        val condition = suspension.condition
        if (!condition.resume()) {
            return
        }
        // Important to remove the current suspension before the continuation is resumed, as the
        // resumed logic may set a new coroutine suspension point.
        this.suspension = null
        val continuation = suspension.continuation
        continuation.resume(condition.value())
    }

    public fun stop(): Nothing {
        suspension = null
        throw CancellationException()
    }

    public fun cancel(exception: CancellationException = CancellationException()) {
        suspension?.continuation?.resumeWithException(exception)
        suspension = null
    }

    public fun resumeWith(value: Any) {
        val condition = suspension?.condition ?: error("Coroutine is not suspended: $this.")
        if (condition is DeferredResumeCondition && condition.type == value::class) {
            condition.set(value)
            advance()
        }
    }

    @Suppress("UNCHECKED_CAST")
    public suspend fun pause(resume: () -> Boolean) {
        if (resume()) return
        suspendCoroutineUninterceptedOrReturn {
            val condition = PredicateResumeCondition(resume)
            suspension = GameCoroutineSuspension(it, condition) as GameCoroutineSuspension<Any>
            COROUTINE_SUSPENDED
        }
    }

    @Suppress("UNCHECKED_CAST")
    public suspend fun <T : Any> pause(await: KClass<T>): T {
        return suspendCoroutineUninterceptedOrReturn {
            val condition = DeferredResumeCondition(await)
            suspension = GameCoroutineSuspension(it, condition) as GameCoroutineSuspension<Any>
            COROUTINE_SUSPENDED
        }
    }

    public override fun toString(): String =
        "GameCoroutine(debugName=$debugName, suspension=$suspension)"
}
