package org.rsmod.server.app

import com.github.michaelbull.logging.InlineLogger
import com.google.common.util.concurrent.AbstractIdleService
import jakarta.inject.Inject
import java.util.concurrent.TimeUnit
import kotlin.system.measureNanoTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.rsmod.game.GameProcess
import org.rsmod.server.app.dispatcher.GameCoroutineScope

private const val GAME_TICK_INTERVAL = 600
const val GAME_THREAD_EXECUTOR_NAME = "game"

class GameService
@Inject
constructor(
    @GameCoroutineScope private val gameScope: CoroutineScope,
    private val process: GameProcess,
) : AbstractIdleService() {
    private val logger = InlineLogger()

    private var excessCycleNanos = 0L

    override fun startUp() {
        gameScope.start(GAME_TICK_INTERVAL)
    }

    override fun shutDown() {
        if (isRunning) {
            gameScope.cancel()
            process.shutDown()
        }
    }

    private fun CoroutineScope.start(delay: Int) = launch {
        process.startUp()
        while (isActive) {
            val elapsedNanos = measureNanoTime { process.cycle() } + excessCycleNanos
            val elapsedMillis = TimeUnit.NANOSECONDS.toMillis(elapsedNanos)
            val overdue = elapsedMillis > delay
            val sleepTime =
                if (overdue) {
                    val elapsedCycleCount = elapsedMillis / delay
                    val upcomingCycleDelay = (elapsedCycleCount + 1) * delay
                    upcomingCycleDelay - elapsedMillis
                } else {
                    delay - elapsedMillis
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
    }
}
