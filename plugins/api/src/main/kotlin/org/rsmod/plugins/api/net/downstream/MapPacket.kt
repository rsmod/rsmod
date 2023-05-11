package org.rsmod.plugins.api.net.downstream

import org.rsmod.game.map.zone.ZoneKey
import org.rsmod.game.protocol.packet.DownstreamPacket

public data class RebuildNormal(
    val gpiInitialization: GPIInitialization?,
    val zone: ZoneKey,
    val xteaList: List<Int>
) : DownstreamPacket
