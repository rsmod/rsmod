package gg.rsmod.game

import com.google.inject.Inject
import gg.rsmod.game.config.InternalConfig
import gg.rsmod.game.coroutine.GameCoroutineScope
import gg.rsmod.game.dispatch.GameJobDispatcher
import gg.rsmod.game.model.client.ClientList
import gg.rsmod.game.model.mob.PlayerList
import gg.rsmod.game.update.task.UpdateTaskList
import java.util.concurrent.TimeUnit
import kotlin.system.measureNanoTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val nanosInMilliseconds = TimeUnit.MILLISECONDS.toNanos(1L)

sealed class GameState {
    object Inactive : GameState()
    object Active : GameState()
    object ShutDown : GameState()
}

class Game @Inject private constructor(
    private val config: InternalConfig,
    private val coroutineScope: GameCoroutineScope,
    private val jobDispatcher: GameJobDispatcher,
    private val updateTaskList: UpdateTaskList,
    private val playerList: PlayerList,
    private val clientList: ClientList
) {

    var state: GameState = GameState.Inactive

    var excessCycleNanos = 0L

    fun start() {
        if (state != GameState.Inactive) {
            error("::start has already been called.")
        }
        val delay = config.gameTickDelay
        state = GameState.Active
        coroutineScope.start(delay.toLong())
    }

    private fun CoroutineScope.start(delay: Long) = launch {
        while (state != GameState.ShutDown) {
            val elapsedNanos = measureNanoTime { gameLogic() }
            val elapsedMillis = trimElapsedTime(elapsedNanos)
            if (elapsedMillis > delay) {
                val elapsedCycleCount = elapsedMillis / delay
                val upcomingCycleDelay = (elapsedCycleCount + 1) * delay
                delay(upcomingCycleDelay - elapsedMillis)
                continue
            }
            delay(delay - elapsedMillis)
        }
    }

    private suspend fun gameLogic() {
        clientList.forEach { it.pollActions(config.actionsPerCycle) }
        jobDispatcher.executeAll()
        updateTaskList.forEach { it.execute() }
        playerList.forEach { it?.flush() }
    }

    private fun trimElapsedTime(elapsedNanos: Long): Int {
        var elapsedMillis = (elapsedNanos / nanosInMilliseconds).toInt()
        excessCycleNanos += elapsedNanos - (elapsedMillis * nanosInMilliseconds)
        if (excessCycleNanos >= nanosInMilliseconds) {
            val excessMillis = (excessCycleNanos / nanosInMilliseconds).toInt()
            elapsedMillis += excessMillis
            excessCycleNanos -= excessMillis * nanosInMilliseconds
        }
        return elapsedMillis
    }
}
