package gg.rsmod.plugins.protocol.packet.server

import io.guthix.buffer.toBitMode
import io.netty.buffer.ByteBuf

class PlayerInfo(
    val playerCoordsAs30Bits: Int,
    val otherPlayerCoords: IntArray
) {
    fun write(buf: ByteBuf): ByteBuf {
        val bitBuf = buf.toBitMode()
        bitBuf.writeBits(playerCoordsAs30Bits, 30)
        otherPlayerCoords.forEach { coords ->
            bitBuf.writeBits(coords, 18)
        }
        return bitBuf.toByteMode()
    }
}
