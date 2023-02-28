package org.rsmod.plugins.api.game

import org.rsmod.game.GameProcess
import org.rsmod.game.client.ClientList
import org.rsmod.game.coroutines.GameCoroutineScope
import org.rsmod.game.events.GameEventBus
import org.rsmod.game.events.publish
import org.rsmod.game.model.mob.list.PlayerList
import org.rsmod.game.model.mob.list.forEachNotNull
import org.rsmod.plugins.api.info.player.PlayerInfoTask
import org.rsmod.plugins.api.net.upstream.handler.UpstreamHandlerTask
import javax.inject.Inject

public class MainGameProcess @Inject constructor(
    @GameProcessScope private val coroutineScope: GameCoroutineScope,
    private val upstreamTask: UpstreamHandlerTask,
    private val gpiTask: PlayerInfoTask,
    private val clients: ClientList,
    private val players: PlayerList,
    private val eventBus: GameEventBus
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
        coroutineScope.advance()
        players.forEachNotNull { player -> player.coroutineScope.advance() }
    }

    private fun clientInput() {
        clients.forEach { client -> client.channel.read() }
        players.forEachNotNull { player ->
            val upstream = player.upstream
            upstreamTask.readAll(player, upstream)
            upstream.clear()
        }
    }

    private fun playerCycle() {
        /* info task should be last step in this cycle */
        gpiTask.execute()
    }

    private fun clientOutput() {
        clients.forEach { client ->
            val downstream = client.player.downstream
            downstream.flush(client.channel)
        }
    }

    private fun endCycle() {
        eventBus.publish(GameProcessEvent.EndCycle)
    }
}
