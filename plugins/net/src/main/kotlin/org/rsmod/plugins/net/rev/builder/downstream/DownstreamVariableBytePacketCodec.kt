package org.rsmod.plugins.net.rev.builder.downstream

import io.netty.buffer.ByteBuf
import org.openrs2.crypto.StreamCipher
import org.rsmod.protocol.packet.VariableByteLengthPacketCodec

class DownstreamVariableBytePacketCodec<T : DownstreamPacket>(
    type: Class<T>,
    opcode: Int,
    private val encoder: (T, ByteBuf) -> Unit
) : VariableByteLengthPacketCodec<T>(type, opcode) {

    override fun decode(buf: ByteBuf, cipher: StreamCipher): T {
        throw NotImplementedError("Downstream packet cannot be decoded.")
    }

    override fun encode(packet: T, buf: ByteBuf, cipher: StreamCipher) {
        encoder.invoke(packet, buf)
    }
}
