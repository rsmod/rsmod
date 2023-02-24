package org.rsmod.plugins.api.cache.type

import io.netty.buffer.ByteBuf
import org.openrs2.buffer.readString

// TODO: replace with struct type that stores params
internal fun ByteBuf.readStruct(): Map<Int, Any> {
    val parameters = mutableMapOf<Int, Any>()
    val count = readUnsignedByte().toInt()
    repeat(count) {
        val readString = readBoolean()
        val key = readUnsignedMedium()
        if (readString) {
            parameters[key] = readString()
        } else {
            parameters[key] = readInt()
        }
    }
    return parameters
}
