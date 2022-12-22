package org.rsmod.plugins.net.js5.downstream

import io.netty.buffer.ByteBuf
import io.netty.buffer.DefaultByteBufHolder

data class Js5GroupResponse(
    val archive: Int,
    val group: Int,
    val urgent: Boolean,
    val data: ByteBuf
) : DefaultByteBufHolder(data)
