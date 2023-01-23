package org.rsmod.protocol.packet

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator

public abstract class FixedLengthPacketCodec<T : Packet>(
    type: Class<T>,
    opcode: Int,
    public val length: Int
) : PacketCodec<T>(type, opcode) {

    override fun isLengthReadable(buf: ByteBuf): Boolean = true

    override fun readLength(buf: ByteBuf): Int = length

    override fun offsetLength(buf: ByteBuf) { /* empty */ }

    override fun setLength(buf: ByteBuf, offsetLengthWriterIndex: Int, length: Int) {
        require(length == this.length) {
            "Length for fixed-length packet does not match. (expected ${this.length}, received $length)"
        }
    }

    override fun allocEncodeBuffer(alloc: ByteBufAllocator, packet: T, preferDirect: Boolean): ByteBuf {
        return if (preferDirect) {
            alloc.ioBuffer(1 + length, 1 + length)
        } else {
            alloc.heapBuffer(1 + length, 1 + length)
        }
    }
}
