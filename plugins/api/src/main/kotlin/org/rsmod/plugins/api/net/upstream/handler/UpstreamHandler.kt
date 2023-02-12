package org.rsmod.plugins.api.net.upstream.handler

import org.rsmod.game.model.mob.Player
import org.rsmod.protocol.game.packet.UpstreamPacket

public abstract class UpstreamHandler<T : UpstreamPacket>(public val type: Class<T>) {

    public abstract fun handle(player: Player, packet: T)
}
