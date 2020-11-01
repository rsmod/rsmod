package org.rsmod.plugins.api.protocol.packet.server

import io.guthix.buffer.toBitMode
import io.netty.buffer.ByteBuf
import org.rsmod.game.message.ServerPacket

class InitialPlayerInfo(
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

class PlayerInfo(
    val buffer: ByteBuf
) : ServerPacket
