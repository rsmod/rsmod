package org.rsmod.plugins.net.rev.builder.downstream

import io.netty.buffer.ByteBuf

data class DownstreamPacketStructure<T : DownstreamPacket>(
    val opcode: Int,
    val length: Int,
    val encoder: (T, ByteBuf) -> Unit
)
