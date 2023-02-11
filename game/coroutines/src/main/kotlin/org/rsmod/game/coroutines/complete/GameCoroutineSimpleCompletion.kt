package org.rsmod.game.coroutines.complete

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException

public object GameCoroutineSimpleCompletion : Continuation<Unit> {

    override val context: CoroutineContext get() = EmptyCoroutineContext

    override fun resumeWith(result: Result<Unit>) {
        result.exceptionOrNull()?.let { if (it !is CancellationException) throw it }
    }
}
