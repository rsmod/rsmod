package org.rsmod.game.coroutines.complete

import org.rsmod.game.coroutines.GameCoroutine
import org.rsmod.game.coroutines.GameCoroutineScope
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

public class GameCoroutineSupervisedCompletion(
    private val scope: GameCoroutineScope,
    private val coroutine: GameCoroutine
) : Continuation<Unit> {

    override val context: CoroutineContext get() = EmptyCoroutineContext

    override fun resumeWith(result: Result<Unit>) {
        scope.supervisedResume(coroutine, result)
    }
}
