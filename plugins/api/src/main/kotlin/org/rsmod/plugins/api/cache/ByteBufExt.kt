package org.rsmod.plugins.api.cache

import io.guthix.buffer.readStringCP1252
import io.guthix.buffer.writeStringCP1252
import io.netty.buffer.ByteBuf

internal fun ByteBuf.readParameters(): Map<Int, Any> {
    val parameters = mutableMapOf<Int, Any>()
    val count = readUnsignedByte().toInt()
    repeat(count) {
        val readString = readBoolean()
        val key = readUnsignedMedium()
        if (readString) {
            parameters[key] = readStringCP1252()
        } else {
            parameters[key] = readInt()
        }
    }
    return parameters
}

internal fun ByteBuf.writeParameters(parameters: Map<Int, Any>) {
    writeByte(parameters.size)
    parameters.forEach { (key, value) ->
        val isString = value is String
        writeByte(if (isString) 1 else 0)
        writeMedium(key)
        if (isString) {
            writeStringCP1252(value as String)
        } else {
            writeInt(value as Int)
        }
    }
}
