package org.rsmod.plugins.api.net.downstream

import org.rsmod.protocol.game.packet.DownstreamPacket

public data class MinimapFlagSet(
    public val lx: Int,
    public val lz: Int
) : DownstreamPacket
