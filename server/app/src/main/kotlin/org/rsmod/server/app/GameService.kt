package org.rsmod.server.app

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.system.measureNanoTime
import kotlinx.coroutines.delay
import org.rsmod.game.GameProcess
import org.rsmod.server.services.concurrent.ScheduledListenerService

class GameService @Inject constructor(private val process: GameProcess) : ScheduledListenerService {
    private val logger = InlineLogger()

    private var excessCycleNanos = 0L
    private val shutdownSignaled = AtomicBoolean(false)

    override suspend fun run() {
        // After `signalShutdown`, `run` may be invoked one final time - return early to avoid
        // redundant processing.
        if (shutdownSignaled.get()) {
            return
        }
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

    override suspend fun setup() {
        // Intentionally call `process.startup` on the game thread instead of in `startup`,
        // which runs on the main application thread.
        process.startup()
    }

    override suspend fun signalShutdown() {
        shutdownSignaled.set(true)
        process.preShutdown()
    }

    override suspend fun startup() {}

    override suspend fun shutdown() {
        logger.info { "Attempting to shut down game service." }
        try {
            process.shutdown()
            logger.info { "Game service successfully shut down." }
        } catch (t: Throwable) {
            logger.error(t) { "Game service failed to shut down." }
        }
    }

    private companion object {
        private const val GAME_TICK_INTERVAL = 600L
        private const val GAME_THREAD_NAME = "game"
    }
}
