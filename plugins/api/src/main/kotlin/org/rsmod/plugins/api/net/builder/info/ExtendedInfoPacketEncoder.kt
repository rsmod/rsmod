package org.rsmod.plugins.api.net.builder.info

import io.netty.buffer.ByteBuf
import org.rsmod.game.model.mob.info.ExtendedInfo

public data class ExtendedInfoPacketEncoder<T : ExtendedInfo>(
    public val bitmask: Int,
    public val encode: (T, ByteBuf) -> Unit
)
