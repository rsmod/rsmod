package org.rsmod.plugins.api.net.downstream

import org.rsmod.game.model.map.Zone
import org.rsmod.protocol.game.packet.DownstreamPacket

public data class RebuildNormal(
    val gpiInitialization: GPIInitialization?,
    val zone: Zone,
    val xteaList: List<Int>
) : DownstreamPacket
