package org.rsmod.plugins.api.net.upstream.handler

import org.rsmod.game.model.UpstreamList
import org.rsmod.game.model.mob.Player
import org.rsmod.game.task.UpstreamTask
import javax.inject.Inject

public class UpstreamHandlerTask @Inject constructor(
    private val handlers: UpstreamHandlerMap
) : UpstreamTask {

    override fun readAll(player: Player, upstream: UpstreamList) {
        upstream.forEach { packet ->
            // TODO: log missing handlers
            val handler = handlers[packet] ?: return@forEach
            handler.handle(player, packet)
        }
    }
}
