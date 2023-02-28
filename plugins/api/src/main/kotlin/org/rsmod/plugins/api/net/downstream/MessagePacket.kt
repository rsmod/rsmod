package org.rsmod.plugins.api.net.downstream

import org.rsmod.protocol.game.packet.DownstreamPacket

public data class MessageGame(
    val text: String,
    val username: String?,
    val type: Int
) : DownstreamPacket
