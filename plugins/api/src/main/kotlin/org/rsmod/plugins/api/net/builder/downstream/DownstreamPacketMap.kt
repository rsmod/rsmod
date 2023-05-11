package org.rsmod.plugins.api.net.builder.downstream

import io.netty.buffer.ByteBuf
import org.rsmod.game.protocol.Protocol
import org.rsmod.game.protocol.packet.DownstreamPacket
import org.rsmod.game.protocol.packet.PacketCodec

public class DownstreamPacketMap(
    public val structures: MutableMap<Class<out DownstreamPacket>, DownstreamPacketStructure<*>> = mutableMapOf()
) {

    private var protocol: Protocol? = null

    public inline fun <reified T : DownstreamPacket> register(init: DownstreamPacketBuilder<T>.() -> Unit) {
        val builder = DownstreamPacketBuilder<T>().apply(init)
        val structure = builder.build()
        check(T::class.java !in structures.keys) {
            "Downstream packet structure already defined (packet=${T::class.simpleName})."
        }
        structures[T::class.java] = structure
    }

    public fun getOrCreateProtocol(): Protocol {
        val protocol = protocol ?: toProtocol()
        this.protocol = protocol
        return protocol
    }

    @Suppress("UNCHECKED_CAST")
    private fun toProtocol(): Protocol {
        val codecs = mutableSetOf<PacketCodec<out DownstreamPacket>>()
        structures.forEach { (type, structure) ->
            val encoder = structure.encoder as (Any, ByteBuf) -> Unit
            val codec = when (structure.length) {
                -1 -> DownstreamVariableBytePacketCodec(type, structure.opcode, encoder)
                -2 -> DownstreamVariableShortPacketCodec(type, structure.opcode, encoder)
                else -> DownstreamFixedLengthPacketCodec(type, structure.opcode, structure.length, encoder)
            }
            codecs += codec
        }
        return Protocol(codecs)
    }
}
