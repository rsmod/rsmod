package org.rsmod.plugins.api.prot.builder.downstream

import io.netty.buffer.ByteBuf

@DslMarker
private annotation class PacketBuilderDsl

@PacketBuilderDsl
class DownstreamPacketBuilder<T : DownstreamPacket> {

    private lateinit var encoder: (T, ByteBuf) -> Unit

    var opcode = -1

    var length = 0

    fun encode(encoder: (packet: T, buf: ByteBuf) -> Unit) {
        this.encoder = encoder
    }

    fun build(): DownstreamPacketStructure<T> {
        check(opcode != -1)
        check(::encoder.isInitialized)
        return DownstreamPacketStructure(opcode, length, encoder)
    }
}
