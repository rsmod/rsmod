package org.rsmod.plugins.api.net.builder.upstream

import io.netty.buffer.ByteBuf
import org.rsmod.game.protocol.packet.UpstreamPacket

private const val VARIABLE_BYTE_LENGTH = -1
private const val VARIABLE_SHORT_LENGTH = -2

@DslMarker
private annotation class PacketBuilderDsl

@PacketBuilderDsl
public class UpstreamPacketBuilder<T : UpstreamPacket> {

    private lateinit var decoder: (ByteBuf) -> T

    public var opcode: Int = -1

    public var length: Int = 0

    public val variableByteLength: Int
        get() = VARIABLE_BYTE_LENGTH

    public val variableShortLength: Int
        get() = VARIABLE_SHORT_LENGTH

    public fun decode(decoder: (buf: ByteBuf) -> T) {
        this.decoder = decoder
    }

    public fun build(): UpstreamPacketStructure<T> {
        check(opcode != -1)
        check(::decoder.isInitialized)
        return UpstreamPacketStructure(opcode, length, decoder)
    }
}
