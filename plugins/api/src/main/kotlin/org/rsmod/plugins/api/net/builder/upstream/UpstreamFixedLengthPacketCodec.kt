package org.rsmod.plugins.api.net.builder.upstream

import io.netty.buffer.ByteBuf
import org.openrs2.crypto.StreamCipher
import org.rsmod.protocol.game.packet.FixedLengthPacketCodec
import org.rsmod.protocol.game.packet.UpstreamPacket

public class UpstreamFixedLengthPacketCodec<T : UpstreamPacket>(
    type: Class<T>,
    opcode: Int,
    length: Int,
    private val decoder: (ByteBuf) -> UpstreamPacket
) : FixedLengthPacketCodec<T>(type, opcode, length) {

    @Suppress("UNCHECKED_CAST")
    override fun decode(buf: ByteBuf, cipher: StreamCipher): T {
        return decoder.invoke(buf) as T
    }

    override fun encode(packet: T, buf: ByteBuf, cipher: StreamCipher) {
        throw NotImplementedError("Upstream packet cannot be encoded.")
    }
}
