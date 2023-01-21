package org.rsmod.plugins.net.rev.builder.upstream

import io.netty.buffer.ByteBuf

data class UpstreamPacketStructure<T : UpstreamPacket>(
    val opcode: Int,
    val length: Int,
    val decoder: (ByteBuf) -> T
)
