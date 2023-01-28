package org.rsmod.plugins.api.prot.builder.upstream

import io.netty.buffer.ByteBuf
import org.rsmod.protocol.packet.UpstreamPacket

@DslMarker
private annotation class PacketBuilderDsl

@PacketBuilderDsl
class UpstreamPacketBuilder<T : UpstreamPacket> {

    private lateinit var decoder: (ByteBuf) -> T

    var opcode = -1

    var length = 0

    fun decode(decoder: (buf: ByteBuf) -> T) {
        this.decoder = decoder
    }

    fun build(): UpstreamPacketStructure<T> {
        check(opcode != -1)
        check(::decoder.isInitialized)
        return UpstreamPacketStructure(opcode, length, decoder)
    }
}
