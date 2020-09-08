package gg.rsmod.plugins.protocol.packet.server

import gg.rsmod.game.model.domain.repo.XteaRepository
import gg.rsmod.game.message.ServerPacket

data class RebuildNormal(
    val gpi: InitializeGpi?,
    val zoneX: Int,
    val zoneY: Int,
    val xteas: XteaRepository
) : ServerPacket

data class InitializeGpi(
    val playerCoordsAs30Bits: Int,
    val otherPlayerCoords: IntArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InitializeGpi

        if (playerCoordsAs30Bits != other.playerCoordsAs30Bits) return false
        if (!otherPlayerCoords.contentEquals(other.otherPlayerCoords)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = playerCoordsAs30Bits
        result = 31 * result + otherPlayerCoords.contentHashCode()
        return result
    }
}
