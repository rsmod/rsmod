package org.rsmod.plugins.api.prot.builder.upstream

import org.rsmod.protocol.Protocol
import org.rsmod.protocol.packet.PacketCodec

class UpstreamPacketMap(
    val structures: MutableMap<Class<out UpstreamPacket>, UpstreamPacketStructure<*>> = mutableMapOf()
) {

    private var protocol: Protocol? = null

    inline fun <reified T : UpstreamPacket> register(init: UpstreamPacketBuilder<T>.() -> Unit) {
        val builder = UpstreamPacketBuilder<T>().apply(init)
        val structure = builder.build()
        check(T::class.java !in structures.keys) {
            "Upstream packet structure already defined (packet=${T::class.simpleName})."
        }
        structures[T::class.java] = structure
    }

    fun getOrCreateProtocol(): Protocol {
        val protocol = protocol ?: toProtocol()
        this.protocol = protocol
        return protocol
    }

    private fun toProtocol(): Protocol {
        val codecs = mutableSetOf<PacketCodec<out UpstreamPacket>>()
        structures.forEach { (type, structure) ->
            val codec = when (structure.length) {
                -1 -> UpstreamVariableBytePacketCodec(type, structure.opcode, structure.decoder)
                -2 -> UpstreamVariableShortPacketCodec(type, structure.opcode, structure.decoder)
                else -> UpstreamFixedLengthPacketCodec(type, structure.opcode, structure.length, structure.decoder)
            }
            codecs += codec
        }
        return Protocol(codecs)
    }
}
