package org.rsmod.plugins.api.net.builder.upstream

import io.netty.buffer.ByteBuf
import org.openrs2.crypto.StreamCipher
import org.rsmod.game.protocol.packet.UpstreamDiscardPacket
import org.rsmod.game.protocol.packet.UpstreamPacket
import org.rsmod.game.protocol.packet.VariableShortLengthPacketCodec

public class UpstreamVariableShortPacketCodec<T : UpstreamPacket>(
    type: Class<T>,
    opcode: Int,
    private val decoder: (ByteBuf) -> UpstreamPacket
) : VariableShortLengthPacketCodec<T>(type, opcode) {

    @Suppress("UNCHECKED_CAST")
    override fun decode(buf: ByteBuf, cipher: StreamCipher): T {
        val packet = decoder.invoke(buf) as T
        if (buf.isReadable && packet is UpstreamDiscardPacket) {
            buf.readBytes(buf.readableBytes())
        }
        return packet
    }

    override fun encode(packet: T, buf: ByteBuf, cipher: StreamCipher) {
        throw NotImplementedError("Upstream packet cannot be encoded.")
    }
}
