package org.rsmod.plugins.api.net.builder.downstream

import io.netty.buffer.ByteBuf
import org.openrs2.crypto.StreamCipher
import org.rsmod.game.protocol.packet.DownstreamPacket
import org.rsmod.game.protocol.packet.VariableShortLengthPacketCodec

public class DownstreamVariableShortPacketCodec<T : DownstreamPacket>(
    type: Class<T>,
    opcode: Int,
    private val encoder: (T, ByteBuf) -> Unit
) : VariableShortLengthPacketCodec<T>(type, opcode) {

    override fun decode(buf: ByteBuf, cipher: StreamCipher): T {
        throw NotImplementedError("Downstream packet cannot be decoded.")
    }

    override fun encode(packet: T, buf: ByteBuf, cipher: StreamCipher) {
        encoder.invoke(packet, buf)
    }
}
