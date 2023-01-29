package org.rsmod.plugins.api.net.downstream

import org.rsmod.game.model.map.Zone
import org.rsmod.plugins.api.prot.info.GPIInitialization
import org.rsmod.protocol.packet.DownstreamPacket

data class RebuildNormal(
    val gpiInitialization: GPIInitialization?,
    val zone: Zone,
    val xteaList: List<Int>
) : DownstreamPacket
