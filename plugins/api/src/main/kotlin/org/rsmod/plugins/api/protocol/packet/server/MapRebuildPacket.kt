package org.rsmod.plugins.api.protocol.packet.server

import org.rsmod.game.message.ServerPacket
import org.rsmod.game.model.domain.repo.XteaRepository
import org.rsmod.game.model.map.MapSquare
import org.rsmod.game.model.map.Zone

data class RebuildNormal(
    val gpi: InitialPlayerInfo?,
    val playerZone: Zone,
    val viewport: List<MapSquare>,
    val xteas: XteaRepository
) : ServerPacket
