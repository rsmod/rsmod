package org.rsmod.api.game.process

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import org.rsmod.api.db.gateway.GameDbSynchronizer
import org.rsmod.events.EventBus
import org.rsmod.game.GameProcess
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList

public class MainGameProcess
@Inject
constructor(
    private val gameCycle: GameCycle,
    private val eventBus: EventBus,
    private val playerList: PlayerList,
    private val dbSync: GameDbSynchronizer,
) : GameProcess {
    override fun startup() {
        eventBus.publish(GameLifecycle.Startup)
    }

    override fun cycle() {
        gameCycle.tick()
    }

    override fun preShutdown() {
        setShutdownFlags()
        fastForwardServices()
        fastForwardCycles()
        logRemainingPlayers()
    }

    override fun shutdown() {
        eventBus.publish(GameLifecycle.Shutdown)
    }

    private fun setShutdownFlags() {
        for (player in playerList) {
            player.pendingShutdown = true
        }
    }

    // Delegates to services to perform fast-forward shutdown. This function may block while
    // services complete their shutdown logic.
    private fun fastForwardServices() {
        dbSync.blockingFastForwardShutdown()
    }

    private fun fastForwardCycles() {
        repeat(SHUTDOWN_MAX_SIMULATIONS) { cycle() }
    }

    private fun logRemainingPlayers() {
        val remaining = playerList.filterNot(Player::loggingOut)
        if (remaining.isNotEmpty()) {
            // Log up to 250 player names to avoid creating excessively large log entries.
            val names = remaining.take(250).joinToString { "'${it.username}'" }
            logger.error { "${remaining.size} players were unable to logout properly: $names" }
        }
    }

    private companion object {
        /**
         * The maximum number of [GameCycle.tick] functions that will be called in a tight,
         * non-delayed loop before the server is shutdown.
         *
         * This is a safeguard to ensure any pending actions in the server are handled appropriately
         * and that players are gracefully logged out before the server shuts down.
         */
        private const val SHUTDOWN_MAX_SIMULATIONS = 1024
        private val logger = InlineLogger()
    }
}
