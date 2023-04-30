package org.rsmod.game.job.boot

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.rsmod.game.dispatcher.io.IOCoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

private typealias Task = suspend CoroutineScope.() -> Unit

@Singleton
public class GameBootTaskScheduler @Inject constructor(private val ioCoroutineScope: IOCoroutineScope) {

    private val blocking: MutableList<Task> = mutableListOf()
    private val nonBlocking: MutableList<Task> = mutableListOf()

    public fun scheduleBlocking(action: suspend CoroutineScope.() -> Unit) {
        blocking += action
    }

    public fun scheduleNonBlocking(action: suspend CoroutineScope.() -> Unit) {
        nonBlocking += action
    }

    public suspend fun executeBlocking(scope: CoroutineScope) {
        blocking.forEach { it.invoke(scope) }
    }

    public suspend fun executeNonBlocking() {
        ioCoroutineScope.executeNonBlocking().join()
    }

    private fun CoroutineScope.executeNonBlocking() = launch {
        nonBlocking.forEach { launch { it(this) } }
    }

    public fun getBlocking(): List<suspend CoroutineScope.() -> Unit> = blocking

    public fun getNonBlocking(): List<suspend CoroutineScope.() -> Unit> = nonBlocking
}
