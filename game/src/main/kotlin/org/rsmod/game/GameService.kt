package org.rsmod.game

import com.github.michaelbull.logging.InlineLogger
import com.google.common.util.concurrent.AbstractIdleService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.rsmod.game.client.ClientList
import org.rsmod.game.dispatcher.main.MainCoroutineScope
import org.rsmod.game.task.PlayerInfoTask
import org.rsmod.game.task.UpstreamTask
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.system.measureNanoTime

private val logger = InlineLogger()

private const val GAME_TICK_DELAY = 600

@Singleton
public class GameService @Inject private constructor(
    private val mainCoroutineScope: MainCoroutineScope,
    private val upstreamTask: UpstreamTask,
    private val gpiTask: PlayerInfoTask,
    private val clients: ClientList
) : AbstractIdleService() {

    private var excessCycleNanos = 0L

    override fun startUp() {
        mainCoroutineScope.start(GAME_TICK_DELAY)
    }

    override fun shutDown() {
        if (isRunning) {
            mainCoroutineScope.cancel()
        }
    }

    private fun CoroutineScope.start(delay: Int) = launch {
        while (isActive) {
            val elapsedNanos = measureNanoTime { gameCycle() } + excessCycleNanos
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

    private suspend fun gameCycle() {
        clients.forEach { client ->
            client.channel.read()
            val upstream = client.player.upstream
            upstreamTask.readAll(client.player, upstream)
            upstream.clear()
        }
        gpiTask.execute()
        clients.forEach { client ->
            val downstream = client.player.downstream
            downstream.flush(client.channel)
        }
    }
}
