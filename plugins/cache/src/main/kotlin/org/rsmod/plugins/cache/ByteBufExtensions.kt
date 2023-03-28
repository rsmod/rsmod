package org.rsmod.plugins.cache

import io.netty.buffer.ByteBuf
import org.openrs2.buffer.readUnsignedShortSmart

internal fun ByteBuf.readIncrUnsignedShortSmart(): Int {
    var value = 0
    var curr = readUnsignedShortSmart()
    while (curr == 0x7FFF) {
        value += curr
        curr = readUnsignedShortSmart()
    }
    value += curr
    return value
}
