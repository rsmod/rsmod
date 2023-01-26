package org.rsmod.plugins.api.prot.builder.upstream

import io.netty.buffer.ByteBuf

data class UpstreamPacketStructure<T : UpstreamPacket>(
    val opcode: Int,
    val length: Int,
    val decoder: (ByteBuf) -> T
)
