package org.rsmod.plugins.api.prot

import org.openrs2.buffer.BitBuf

data class GPIInitialization(
    val playerCoordsAs30Bits: Int,
    val otherPlayerCoordsAs18Bits: IntArray
) {

    fun encode(buf: BitBuf) {
        buf.writeBits(len = 30, value = playerCoordsAs30Bits)
        otherPlayerCoordsAs18Bits.forEach { coords ->
            buf.writeBits(len = 18, value = coords)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GPIInitialization

        if (playerCoordsAs30Bits != other.playerCoordsAs30Bits) return false
        return otherPlayerCoordsAs18Bits.contentEquals(other.otherPlayerCoordsAs18Bits)
    }

    override fun hashCode(): Int {
        var result = playerCoordsAs30Bits
        result = 31 * result + otherPlayerCoordsAs18Bits.contentHashCode()
        return result
    }
}
