package org.rsmod.protocol

import org.rsmod.protocol.packet.Packet
import org.rsmod.protocol.packet.PacketCodec
import javax.inject.Inject

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
