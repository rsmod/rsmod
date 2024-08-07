package org.rsmod.plugins.api.net.upstream.handler

import jakarta.inject.Inject
import org.rsmod.game.model.UpstreamList
import org.rsmod.game.model.mob.Player

public class UpstreamHandlerTask @Inject constructor(
    private val handlers: UpstreamHandlerMap
) {

    public fun readAll(player: Player, upstream: UpstreamList) {
        upstream.forEach { packet ->
            // TODO: log missing handlers
            val handler = handlers[packet] ?: return@forEach
            handler.handle(player, packet)
        }
    }
}
