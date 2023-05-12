package org.rsmod.plugins.api.net.upstream

import org.rsmod.game.protocol.packet.UpstreamPacket

public data class OpLoc1(
    public val mode: Int,
    public val id: Int,
    public val x: Int,
    public val z: Int
) : UpstreamPacket
