package org.rsmod.plugins.api.net.downstream

import org.rsmod.game.protocol.packet.DownstreamPacket

public data class MinimapFlagSet(
    public val lx: Int,
    public val lz: Int
) : DownstreamPacket
