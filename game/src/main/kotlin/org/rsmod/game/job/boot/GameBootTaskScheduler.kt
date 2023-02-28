package org.rsmod.game.job.boot

import kotlinx.coroutines.launch
import org.rsmod.game.dispatcher.io.IOCoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class GameBootTaskScheduler @Inject constructor(
    private val ioCoroutineScope: IOCoroutineScope
) {

    private val blocking: MutableList<suspend () -> Unit> = mutableListOf()
    private val nonBlocking: MutableList<suspend () -> Unit> = mutableListOf()

    public fun scheduleBlocking(action: suspend () -> Unit) {
        blocking += action
    }

    public fun scheduleNonBlocking(action: suspend () -> Unit) {
        nonBlocking += action
    }

    public suspend fun executeBlocking() {
        blocking.forEach { it.invoke() }
    }

    public suspend fun executeNonBlocking() {
        val job = ioCoroutineScope.launch {
            nonBlocking.forEach {
                launch { it.invoke() }
            }
        }
        job.join()
    }

    public fun getBlocking(): List<suspend () -> Unit> = blocking

    public fun getNonBlocking(): List<suspend () -> Unit> = nonBlocking
}
