package org.rsmod.game.job.boot

import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.rsmod.game.dispatcher.io.IOCoroutineScope

private typealias Task = suspend CoroutineScope.() -> Unit

@Singleton
public class GameBootTaskScheduler @Inject constructor(private val ioCoroutineScope: IOCoroutineScope) {

    private val _blocking: MutableList<Task> = mutableListOf()
    private val _nonBlocking: MutableList<Task> = mutableListOf()

    public val blocking: List<suspend CoroutineScope.() -> Unit> get() = _blocking
    public val nonBlocking: List<suspend CoroutineScope.() -> Unit> get() = _nonBlocking

    public fun scheduleBlocking(action: suspend CoroutineScope.() -> Unit) {
        _blocking += action
    }

    public fun scheduleNonBlocking(action: suspend CoroutineScope.() -> Unit) {
        _nonBlocking += action
    }

    public suspend fun executeBlocking(scope: CoroutineScope) {
        _blocking.forEach { it.invoke(scope) }
    }

    public suspend fun executeNonBlocking() {
        ioCoroutineScope.executeNonBlocking().join()
    }

    private fun CoroutineScope.executeNonBlocking() = launch {
        _nonBlocking.forEach { launch { it(this) } }
    }
}
