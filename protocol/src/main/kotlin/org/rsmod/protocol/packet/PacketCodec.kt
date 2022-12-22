package org.rsmod.protocol.packet

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import org.rsmod.crypto.StreamCipher

public abstract class PacketCodec<T : Packet>(
    public val type: Class<T>,
    public val opcode: Int
) {

    public abstract fun decode(buf: ByteBuf, cipher: StreamCipher): T

    public abstract fun encode(packet: T, buf: ByteBuf, cipher: StreamCipher)

    public abstract fun isLengthReadable(buf: ByteBuf): Boolean

    public abstract fun readLength(buf: ByteBuf): Int

    public abstract fun offsetLength(buf: ByteBuf)

    public abstract fun setLength(buf: ByteBuf, offsetLengthWriterIndex: Int, length: Int)

    public open fun allocEncodeBuffer(alloc: ByteBufAllocator, packet: T, preferDirect: Boolean): ByteBuf {
        return if (preferDirect) {
            alloc.ioBuffer()
        } else {
            alloc.heapBuffer()
        }
    }
}
