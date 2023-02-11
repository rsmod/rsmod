package org.rsmod.game.coroutines

import org.rsmod.game.coroutines.resume.DeferResumeCondition
import org.rsmod.game.coroutines.resume.PredicateResumeCondition
import org.rsmod.game.coroutines.resume.TickResumeCondition
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.reflect.KClass

@RestrictsSuspension
@Suppress("UNCHECKED_CAST")
public class GameCoroutine(public val debugName: String? = null) {

    private var suspension: GameCoroutineSuspension<Any>? = null

    public val isIdle: Boolean get() = !isSuspended

    public val isSuspended: Boolean get() = suspension != null

    public fun resume() {
        val suspension = suspension ?: return
        val resume = suspension.resume()
        if (resume && this.suspension === suspension) {
            this.suspension = null
        }
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
        val condition = suspension?.condition ?: error("Coroutine not suspended: $this")
        if (condition !is DeferResumeCondition) return
        if (condition.type != value::class) return
        condition.set(value)
        resume()
    }

    public suspend fun pause(ticks: Int) {
        if (ticks <= 0) return
        suspendCoroutineUninterceptedOrReturn {
            val condition = TickResumeCondition(ticks)
            suspension = GameCoroutineSuspension(it, condition) as GameCoroutineSuspension<Any>
            COROUTINE_SUSPENDED
        }
    }

    public suspend fun pause(resume: () -> Boolean) {
        if (resume()) return
        suspendCoroutineUninterceptedOrReturn {
            val condition = PredicateResumeCondition(resume)
            suspension = GameCoroutineSuspension(it, condition) as GameCoroutineSuspension<Any>
            COROUTINE_SUSPENDED
        }
    }

    public suspend fun <T : Any> pause(type: KClass<T>): T {
        return suspendCoroutineUninterceptedOrReturn {
            val condition = DeferResumeCondition(type)
            suspension = GameCoroutineSuspension(it, condition) as GameCoroutineSuspension<Any>
            COROUTINE_SUSPENDED
        }
    }

    override fun toString(): String {
        return "GameCoroutine(debugName=$debugName, continuation=$suspension)"
    }

    private companion object {

        private fun <T> GameCoroutineSuspension<T>.resume(): Boolean {
            val deferred = condition.resumeOrNull() ?: return false
            continuation.resume(deferred)
            return true
        }
    }
}
