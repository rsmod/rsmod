package org.rsmod.game.protocol

import jakarta.inject.Inject
import org.rsmod.game.protocol.packet.Packet
import org.rsmod.game.protocol.packet.PacketCodec

public class Protocol @Inject constructor(codecs: Set<PacketCodec<*>>) {

    private val decoders = codecs.associateBy { it.opcode }

    private val encoders = codecs.associateBy { it.type }

    public fun getDecoder(opcode: Int): PacketCodec<*>? {
        return decoders[opcode]
    }

    @Suppress("UNCHECKED_CAST")
    public fun <T : Packet> getEncoder(type: Class<T>): PacketCodec<T>? {
        return encoders[type] as? PacketCodec<T>
    }
}
