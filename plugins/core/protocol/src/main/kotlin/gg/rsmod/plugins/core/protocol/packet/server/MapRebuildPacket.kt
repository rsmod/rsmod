package gg.rsmod.plugins.core.protocol.packet.server

import gg.rsmod.game.message.ServerPacket
import gg.rsmod.game.model.domain.repo.XteaRepository
import gg.rsmod.game.model.map.MapSquare
import gg.rsmod.game.model.map.Zone

data class RebuildNormal(
    val gpi: InitialPlayerInfo?,
    val playerZone: Zone,
    val viewport: List<MapSquare>,
    val xteas: XteaRepository
) : ServerPacket
