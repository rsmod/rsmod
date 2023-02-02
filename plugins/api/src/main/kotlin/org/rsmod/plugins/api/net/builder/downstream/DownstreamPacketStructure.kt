package org.rsmod.plugins.api.net.builder.downstream

import io.netty.buffer.ByteBuf
import org.rsmod.protocol.game.packet.DownstreamPacket

data class DownstreamPacketStructure<T : DownstreamPacket>(
    val opcode: Int,
    val length: Int,
    val encoder: (T, ByteBuf) -> Unit
)
