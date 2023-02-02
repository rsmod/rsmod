package org.rsmod.plugins.api.prot.info

import org.openrs2.buffer.BitBuf

public data class GPIInitialization(
    val playerCoordsAs30Bits: Int,
    val otherPlayerCoordsAs18Bits: List<Int>
) {

    public fun encode(buf: BitBuf) {
        buf.writeBits(len = 30, value = playerCoordsAs30Bits)
        otherPlayerCoordsAs18Bits.forEach { coords ->
            buf.writeBits(len = 18, value = coords)
        }
    }
}
