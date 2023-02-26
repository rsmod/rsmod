package org.rsmod.plugins.api.cache.type

import io.netty.buffer.ByteBuf
import org.openrs2.buffer.readString
import org.rsmod.plugins.api.cache.type.param.ParamMap
import org.rsmod.plugins.api.cache.type.param.ParamTypeList

internal fun ByteBuf.readParams(types: ParamTypeList): ParamMap {
    val parameters = mutableMapOf<Int, Any>()
    val count = readUnsignedByte().toInt()
    repeat(count) {
        val isString = readBoolean()
        val key = readUnsignedMedium()
        val param = types[key] ?: error("Param with id `$key` not found in cache.")
        if (isString) {
            val value = readString()
            val decoded = param.type?.decodeString(value)
            parameters[key] = decoded ?: value
        } else {
            val value = readInt()
            val decoded = param.type?.decodeInt(value)
            parameters[key] = decoded ?: value
        }
    }
    return ParamMap(parameters)
}
