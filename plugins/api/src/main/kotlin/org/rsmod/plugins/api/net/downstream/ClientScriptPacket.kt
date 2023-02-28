package org.rsmod.plugins.api.net.downstream

import org.rsmod.protocol.game.packet.DownstreamPacket

public data class RunClientScript(
    val id: Int,
    val args: List<Any>
) : DownstreamPacket
