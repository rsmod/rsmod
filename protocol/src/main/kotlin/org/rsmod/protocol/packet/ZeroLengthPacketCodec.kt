package org.rsmod.protocol.packet

import io.netty.buffer.ByteBuf
import org.rsmod.crypto.StreamCipher

public abstract class ZeroLengthPacketCodec<T : Packet>(
    private val packet: T,
    opcode: Int
) : FixedLengthPacketCodec<T>(packet.javaClass, opcode, length = 0) {

    override fun decode(buf: ByteBuf, cipher: StreamCipher): T = packet

    override fun encode(packet: T, buf: ByteBuf, cipher: StreamCipher) { /* empty */ }
}
