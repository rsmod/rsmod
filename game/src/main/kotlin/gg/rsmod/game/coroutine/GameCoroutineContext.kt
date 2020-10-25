package gg.rsmod.game.coroutine

import com.github.michaelbull.logging.InlineLogger
import java.util.concurrent.CancellationException
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.createCoroutine
import kotlin.coroutines.resume

private val logger = InlineLogger()

internal fun (suspend () -> Unit).launchCoroutine() {
    createCoroutine(GameCoroutineContinuation).resume(Unit)
}

internal class GameCoroutineContext<T>(private val continuation: Continuation<T>) {

    fun resume(value: T) {
        continuation.resume(value)
    }
}

internal object GameCoroutineContinuation : Continuation<Unit> {

    override val context: CoroutineContext = EmptyCoroutineContext

    override fun resumeWith(result: Result<Unit>) {
        val error = result.exceptionOrNull()
        if (error != null && error !is CancellationException) {
            logger.error(error) {}
        }
    }
}
