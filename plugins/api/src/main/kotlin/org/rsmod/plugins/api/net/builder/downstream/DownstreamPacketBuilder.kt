package org.rsmod.plugins.api.net.builder.downstream

import io.netty.buffer.ByteBuf
import org.rsmod.game.protocol.packet.DownstreamPacket

private const val VARIABLE_BYTE_LENGTH = -1
private const val VARIABLE_SHORT_LENGTH = -2

@DslMarker
private annotation class PacketBuilderDsl

@PacketBuilderDsl
public class DownstreamPacketBuilder<T : DownstreamPacket> {

    private lateinit var encoder: (T, ByteBuf) -> Unit

    public var opcode: Int = -1

    public var length: Int = 0

    public val variableByteLength: Int
        get() = VARIABLE_BYTE_LENGTH

    public val variableShortLength: Int
        get() = VARIABLE_SHORT_LENGTH

    public fun encode(encoder: (packet: T, buf: ByteBuf) -> Unit) {
        this.encoder = encoder
    }

    public fun build(): DownstreamPacketStructure<T> {
        check(opcode != -1)
        check(::encoder.isInitialized)
        return DownstreamPacketStructure(opcode, length, encoder)
    }
}
