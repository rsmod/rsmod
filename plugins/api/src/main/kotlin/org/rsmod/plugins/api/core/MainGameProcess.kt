package org.rsmod.plugins.api.core

import org.rsmod.game.GameProcess
import org.rsmod.game.client.ClientList
import org.rsmod.game.coroutines.GameCoroutineScope
import org.rsmod.game.events.EventBus
import org.rsmod.game.model.WorldClock
import org.rsmod.game.model.mob.list.PlayerList
import org.rsmod.game.model.mob.list.forEachNotNull
import org.rsmod.plugins.api.info.player.PlayerInfoTask
import org.rsmod.plugins.api.move.MovementProcess
import org.rsmod.plugins.api.net.upstream.handler.UpstreamHandlerTask
import javax.inject.Inject

public class MainGameProcess @Inject constructor(
    @GameProcessScope private val coroutineScope: GameCoroutineScope,
    private val upstreamTask: UpstreamHandlerTask,
    private val gpiTask: PlayerInfoTask,
    private val clients: ClientList,
    private val players: PlayerList,
    private val eventBus: EventBus,
    private val movement: MovementProcess,
    private val clock: WorldClock
) : GameProcess {

    override fun startUp() {
        eventBus.publish(GameProcessEvent.BootUp)
    }

    override fun shutDown() {
        // TODO: unregister players and await responses.
        coroutineScope.cancel()
    }

    override fun cycle() {
        startCycle()
        worldCycle()
        clientInput()
        playerCycle()
        clientOutput()
        endCycle()
    }

    private fun startCycle() {
        eventBus.publish(GameProcessEvent.StartCycle)
    }

    private fun worldCycle() {
        clock.tick++
        coroutineScope.advance()
        players.advanceCoroutineScope()
    }

    private fun clientInput() {
        clients.readChannels()
        players.readUpstream()
    }

    private fun playerCycle() {
        players.publishEvents()
        movement.execute()
        /* info task should be last step in this cycle */
        gpiTask.execute()
    }

    private fun clientOutput() {
        clients.flushDownstream()
    }

    private fun endCycle() {
        eventBus.publish(GameProcessEvent.EndCycle)
    }

    private fun PlayerList.advanceCoroutineScope() {
        forEachNotNull { player -> player.coroutineScope.advance() }
    }

    private fun ClientList.readChannels() {
        forEach { client -> client.channel.read() }
    }

    private fun PlayerList.readUpstream() {
        forEachNotNull { player ->
            val upstream = player.upstream
            upstreamTask.readAll(player, upstream)
            upstream.clear()
        }
    }

    private fun PlayerList.publishEvents() {
        forEachNotNull { player ->
            val events = player.events
            events.publishAll(player, eventBus)
            events.clear()
        }
    }

    private fun ClientList.flushDownstream() {
        forEach { client ->
            val downstream = client.player.downstream
            downstream.flush(client.channel)
            downstream.clear()
        }
    }
}
