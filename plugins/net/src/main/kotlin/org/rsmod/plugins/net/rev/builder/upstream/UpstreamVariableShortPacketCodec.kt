package org.rsmod.plugins.net.rev.builder.upstream

import io.netty.buffer.ByteBuf
import org.openrs2.crypto.StreamCipher
import org.rsmod.protocol.packet.VariableShortLengthPacketCodec

class UpstreamVariableShortPacketCodec<T : UpstreamPacket>(
    type: Class<T>,
    opcode: Int,
    private val decoder: (ByteBuf) -> UpstreamPacket
) : VariableShortLengthPacketCodec<T>(type, opcode) {

    @Suppress("UNCHECKED_CAST")
    override fun decode(buf: ByteBuf, cipher: StreamCipher): T {
        return decoder.invoke(buf) as T
    }

    override fun encode(packet: T, buf: ByteBuf, cipher: StreamCipher) {
        throw NotImplementedError("Upstream packet cannot be encoded.")
    }
}
