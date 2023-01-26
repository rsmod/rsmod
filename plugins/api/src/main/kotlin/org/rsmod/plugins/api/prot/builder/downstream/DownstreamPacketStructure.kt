package org.rsmod.plugins.api.prot.builder.downstream

import io.netty.buffer.ByteBuf

data class DownstreamPacketStructure<T : DownstreamPacket>(
    val opcode: Int,
    val length: Int,
    val encoder: (T, ByteBuf) -> Unit
)
