package org.rsmod.plugins.api.net.upstream

import org.rsmod.protocol.game.packet.UpstreamPacket

public data class MoveGameClick(
    val mode: Int,
    val x: Int,
    val y: Int
) : UpstreamPacket
