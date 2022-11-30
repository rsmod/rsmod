package org.rsmod.game.coroutines

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.reflect.KClass

private val CoroutineContext.gameCoroutineElement: GameCoroutineContextElement
    get() = get(GameCoroutineContextElement.Companion.CoroutineContextKey) ?: error(
        """
        Game coroutine context element has not been set.
        Construct one and call the suspend block with `withContext(${GameCoroutineContextElement::class.simpleName})`
        """.trimIndent()
    )

public suspend fun stop(): Nothing {
    coroutineContext.gameCoroutineElement.cancel()
}

public suspend fun pause(ticks: Int = 1) {
    if (ticks <= 0) return
    return suspendCoroutineUninterceptedOrReturn {
        it.context.gameCoroutineElement.pause(ticks, it)
        COROUTINE_SUSPENDED
    }
}

public suspend fun pause(condition: () -> Boolean) {
    if (!condition()) return
    return suspendCoroutineUninterceptedOrReturn {
        it.context.gameCoroutineElement.pause(condition, it)
        COROUTINE_SUSPENDED
    }
}

public suspend fun <T : Any> pause(type: KClass<T>): T {
    return suspendCoroutineUninterceptedOrReturn {
        it.context.gameCoroutineElement.pause(type, it)
        COROUTINE_SUSPENDED
    }
}
