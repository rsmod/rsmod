package gg.rsmod.plugins.protocol.packet.server

import gg.rsmod.game.message.ServerPacket
import gg.rsmod.game.model.domain.repo.XteaRepository

data class RebuildNormal(
    val gpi: PlayerInfo?,
    val zoneX: Int,
    val zoneY: Int,
    val xteas: XteaRepository
) : ServerPacket
