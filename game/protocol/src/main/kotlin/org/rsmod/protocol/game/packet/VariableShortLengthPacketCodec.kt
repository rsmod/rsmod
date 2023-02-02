package org.rsmod.protocol.game.packet

import io.netty.buffer.ByteBuf

private const val MAX_SHORT_LENGTH = 65535

public abstract class VariableShortLengthPacketCodec<T : Packet>(
    type: Class<T>,
    opcode: Int
) : PacketCodec<T>(type, opcode) {

    override fun isLengthReadable(buf: ByteBuf): Boolean {
        return buf.readableBytes() >= 2
    }

    override fun readLength(buf: ByteBuf): Int {
        return buf.readUnsignedShort()
    }

    override fun offsetLength(buf: ByteBuf) {
        buf.writeZero(2)
    }

    override fun setLength(buf: ByteBuf, offsetLengthWriterIndex: Int, length: Int) {
        require(length <= MAX_SHORT_LENGTH)
        buf.setShort(offsetLengthWriterIndex, length)
    }
}
