package org.rsmod.plugins.api.protocol.packet.server

import org.rsmod.game.message.ServerPacket

inline class SetMapFlag(private val packed: Int) : ServerPacket {

    val x: Int
        get() = packed and 0xFFFF

    val y: Int
        get() = (packed shr 16) and 0xFFFF

    constructor(x: Int, y: Int) : this(
        (x and 0xFFFF) or ((y and 0xFFFF) shl 16)
    )
}
