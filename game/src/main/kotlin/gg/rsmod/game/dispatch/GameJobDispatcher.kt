package gg.rsmod.game.dispatch

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import gg.rsmod.game.config.InternalConfig
import gg.rsmod.game.coroutine.GameCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val logger = InlineLogger()

private sealed class DispatcherState
private object DispatcherInactive : DispatcherState()
private object DispatcherActive : DispatcherState()

private class GameDispatchJob(
    val block: suspend CoroutineScope.() -> Unit,
    val singleExecute: Boolean
)

class GameJobDispatcher @Inject constructor(
    private val config: InternalConfig,
    private val coroutineScope: GameCoroutineScope
) {
    private var state: DispatcherState = DispatcherInactive

    private val jobs = mutableListOf<GameDispatchJob>()

    fun start() {
        if (state != DispatcherInactive) {
            error("::start has already been called.")
        }
        state = DispatcherActive
        val delay = config.gameTickDelay
        coroutineScope.start(delay.toLong())
    }

    fun schedule(block: suspend CoroutineScope.() -> Unit) {
        val job = GameDispatchJob(block = block, singleExecute = false)
        logger.debug { "Schedule continuous dispatcher job (totalJobs=${jobs.size})" }
        jobs.add(job)
    }

    fun execute(block: suspend CoroutineScope.() -> Unit) {
        val job = GameDispatchJob(block = block, singleExecute = true)
        logger.debug { "Schedule one-time dispatcher job (totalJobs=${jobs.size})" }
        jobs.add(job)
    }

    private fun CoroutineScope.start(delay: Long) = launch {
        while (true) {
            jobs.forEach { job ->
                job.block(this)
            }
            jobs.removeIf { it.singleExecute }
            delay(delay)
        }
    }
}
