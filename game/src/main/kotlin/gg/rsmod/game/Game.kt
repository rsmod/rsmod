package gg.rsmod.game

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import gg.rsmod.game.config.InternalConfig
import gg.rsmod.game.coroutine.GameCoroutineScope
import gg.rsmod.game.coroutine.IoCoroutineScope
import gg.rsmod.game.dispatch.GameJobDispatcher
import gg.rsmod.game.event.impl.PlayerTimerEvent
import gg.rsmod.game.model.client.ClientList
import gg.rsmod.game.model.mob.Player
import gg.rsmod.game.model.mob.PlayerList
import gg.rsmod.game.model.world.World
import gg.rsmod.game.task.StartupTaskList
import gg.rsmod.game.update.task.UpdateTaskList
import java.util.concurrent.TimeUnit
import kotlin.system.measureNanoTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private val logger = InlineLogger()

sealed class GameState {
    object Inactive : GameState()
    object Active : GameState()
    object ShutDown : GameState()
}

class Game @Inject private constructor(
    private val config: InternalConfig,
    private val coroutineScope: GameCoroutineScope,
    private val ioCoroutineScope: IoCoroutineScope,
    private val startupTasks: StartupTaskList,
    private val jobDispatcher: GameJobDispatcher,
    private val updateTaskList: UpdateTaskList,
    private val playerList: PlayerList,
    private val clientList: ClientList,
    private val world: World
) {

    var state: GameState = GameState.Inactive

    var excessCycleNanos = 0L

    fun start() {
        if (state != GameState.Inactive) {
            error("::start has already been called.")
        }
        val delay = config.gameTickDelay
        state = GameState.Active
        startupTasks.start()
        coroutineScope.start(delay.toLong())
    }

    private fun CoroutineScope.start(delay: Long) = launch {
        while (state != GameState.ShutDown) {
            val elapsedNanos = measureNanoTime { gameLogic() } + excessCycleNanos
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

    private suspend fun gameLogic() {
        clientList.forEach { it.pollActions(config.actionsPerCycle) }
        playerList.forEach { it?.queueStack?.cycle() }
        playerList.forEach { it?.timerCycle() }
        world.queueList.cycle()
        jobDispatcher.executeAll()
        updateTaskList.forEach { it.execute() }
        playerList.forEach { it?.flush() }
    }

    private fun StartupTaskList.start() = runBlocking {
        logger.debug { "Executing non-blocking start up tasks (size=${nonBlocking.size})" }
        val ioJob = ioCoroutineScope.launch {
            nonBlocking.forEach {
                launch { it.block() }
            }
        }
        ioJob.join()
        logger.debug { "Executing blocking start up tasks (size=${blocking.size})" }
        blocking.forEach { it.block() }
    }
}

private fun Player.timerCycle() {
    if (timers.isEmpty()) {
        return
    }
    val iterator = timers.iterator()
    while (iterator.hasNext()) {
        val entry = iterator.next()
        val key = entry.key
        val cycles = entry.value
        if (cycles > 0) {
            timers.decrement(key)
            continue
        }
        val event = PlayerTimerEvent(this, key)
        eventBus.publish(event)
        /* if the timer was not re-set after event we remove it */
        if (timers.isNotActive(key)) {
            iterator.remove()
        }
    }
}
