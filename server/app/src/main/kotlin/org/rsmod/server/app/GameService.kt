package org.rsmod.server.app

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import kotlin.system.measureNanoTime
import kotlinx.coroutines.delay
import org.rsmod.game.GameProcess
import org.rsmod.server.services.concurrent.ScheduledService

class GameService @Inject constructor(private val process: GameProcess) : ScheduledService {
    private val logger = InlineLogger()
    private var excessCycleNanos = 0L

    override suspend fun run() {
        val elapsedNanos = measureNanoTime { process.cycle() } + excessCycleNanos
        val elapsedMillis = TimeUnit.NANOSECONDS.toMillis(elapsedNanos)
        val overdue = elapsedMillis > GAME_TICK_INTERVAL
        val sleepTime =
            if (overdue) {
                val elapsedCycleCount = elapsedMillis / GAME_TICK_INTERVAL
                val upcomingCycleDelay = (elapsedCycleCount + 1) * GAME_TICK_INTERVAL
                upcomingCycleDelay - elapsedMillis
            } else {
                GAME_TICK_INTERVAL - elapsedMillis
            }
        if (overdue) {
            logger.error {
                "Cycle took too long (elapsed=${elapsedMillis}ms, sleep=${sleepTime}ms)"
            }
        } else {
            logger.trace { "Cycle took ${elapsedMillis}ms, sleep=${sleepTime}ms" }
        }
        excessCycleNanos = elapsedNanos - TimeUnit.MILLISECONDS.toNanos(elapsedMillis)
        delay(sleepTime)
    }

    override fun createExecutor(): ExecutorService {
        val threadFactory = ThreadFactory { runnable ->
            Thread(runnable, GAME_THREAD_NAME).apply { isDaemon = false }
        }
        return Executors.newSingleThreadExecutor(threadFactory)
    }

    override suspend fun startup() {
        process.startUp()
    }

    override suspend fun shutdown() {
        logger.info { "Attempting to shut down game service." }
        try {
            process.shutDown()
            logger.info { "Game service successfully shut down." }
        } catch (t: Throwable) {
            logger.error(t) { "Game service failed to shut down." }
        }
    }

    private companion object {
        private const val GAME_TICK_INTERVAL = 600
        private const val GAME_THREAD_NAME = "game"
    }
}
