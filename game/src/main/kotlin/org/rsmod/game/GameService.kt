package org.rsmod.game

import com.github.michaelbull.logging.InlineLogger
import com.google.common.util.concurrent.AbstractIdleService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.rsmod.game.dispatcher.main.MainCoroutineScope
import org.rsmod.game.job.boot.GameBootTaskScheduler
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.system.measureNanoTime

private val logger = InlineLogger()

private const val GAME_TICK_DELAY = 600

@Singleton
public class GameService @Inject private constructor(
    private val coroutineScope: MainCoroutineScope,
    private val bootTasks: GameBootTaskScheduler,
    private val process: GameProcess
) : AbstractIdleService() {

    private var excessCycleNanos = 0L

    override fun startUp() {
        bootTasks.execute()
        process.startUp()
        coroutineScope.start(GAME_TICK_DELAY)
    }

    override fun shutDown() {
        if (isRunning) {
            coroutineScope.cancel()
            process.shutDown()
        }
    }

    private fun CoroutineScope.start(delay: Int) = launch {
        while (isActive) {
            val elapsedNanos = measureNanoTime { process.cycle() } + excessCycleNanos
            val elapsedMillis = TimeUnit.NANOSECONDS.toMillis(elapsedNanos)
            val overdue = elapsedMillis > delay
            val sleepTime = if (overdue) {
                val elapsedCycleCount = elapsedMillis / delay
                val upcomingCycleDelay = (elapsedCycleCount + 1) * delay
                upcomingCycleDelay - elapsedMillis
            } else {
                delay - elapsedMillis
            }
            if (overdue) logger.error { "Cycle took too long (elapsed=${elapsedMillis}ms, sleep=${sleepTime}ms)" }
            excessCycleNanos = elapsedNanos - TimeUnit.MILLISECONDS.toNanos(elapsedMillis)
            delay(sleepTime)
        }
    }

    private fun GameBootTaskScheduler.execute(): Unit = runBlocking {
        executeNonBlocking()
        executeBlocking()
    }
}
