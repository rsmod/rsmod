package org.rsmod.protocol.packet

import io.netty.buffer.ByteBuf

private const val MAX_BYTE_LENGTH = 255

public abstract class VariableByteLengthPacketCodec<T : Packet>(
    type: Class<T>,
    opcode: Int
) : PacketCodec<T>(type, opcode) {

    override fun isLengthReadable(buf: ByteBuf): Boolean {
        return buf.isReadable
    }

    override fun readLength(buf: ByteBuf): Int {
        return buf.readUnsignedByte().toInt()
    }

    override fun offsetLength(buf: ByteBuf) {
        buf.writeByte(0)
    }

    override fun setLength(buf: ByteBuf, offsetLengthWriterIndex: Int, length: Int) {
        check(length <= MAX_BYTE_LENGTH)
        buf.setByte(offsetLengthWriterIndex, length)
    }
}
