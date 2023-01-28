package org.rsmod.plugins.api.prot.builder.upstream

import io.netty.buffer.ByteBuf
import org.rsmod.protocol.packet.UpstreamPacket

data class UpstreamPacketStructure<T : UpstreamPacket>(
    val opcode: Int,
    val length: Int,
    val decoder: (ByteBuf) -> T
)
