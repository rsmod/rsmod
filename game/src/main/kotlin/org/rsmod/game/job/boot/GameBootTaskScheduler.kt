package org.rsmod.game.job.boot

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.rsmod.game.dispatcher.io.IOCoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class GameBootTaskScheduler @Inject constructor(
    private val ioCoroutineScope: IOCoroutineScope
) {

    private val blocking: MutableList<suspend (CoroutineScope).() -> Unit> = mutableListOf()
    private val nonBlocking: MutableList<suspend (CoroutineScope).() -> Unit> = mutableListOf()

    public fun scheduleBlocking(action: suspend (CoroutineScope).() -> Unit) {
        blocking += action
    }

    public fun scheduleNonBlocking(action: suspend (CoroutineScope).() -> Unit) {
        nonBlocking += action
    }

    public suspend fun executeBlocking(scope: CoroutineScope) {
        blocking.forEach { it.invoke(scope) }
    }

    public suspend fun executeNonBlocking() {
        val job = ioCoroutineScope.launch {
            nonBlocking.forEach {
                launch { it.invoke(this) }
            }
        }
        job.join()
    }

    public fun getBlocking(): List<suspend (CoroutineScope).() -> Unit> = blocking

    public fun getNonBlocking(): List<suspend (CoroutineScope).() -> Unit> = nonBlocking
}
