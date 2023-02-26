package org.rsmod.plugins.api.cache.type

import io.netty.buffer.ByteBuf
import org.openrs2.buffer.readString
import org.openrs2.buffer.writeString
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

internal fun ByteBuf.writeParams(params: ParamMap, types: ParamTypeList): ByteBuf {
    writeByte(params.size)
    params.forEach { (key, value) ->
        val param = types[key] ?: error("Param with id `$key` not found in cache.")
        val isString = (param.type?.isString) ?: (value is String)
        writeBoolean(isString)
        writeMedium(key)
        if (isString) {
            val encoded = param.type?.encodeString(value)
            val converted = when {
                encoded != null -> encoded
                value is String -> value
                else -> error("Could not encode string value for `$value`. (param=$param)")
            }
            writeString(converted)
        } else {
            val encoded = param.type?.encodeInt(value)
            val converted = when {
                encoded != null -> encoded
                value is Int -> value
                else -> error("Could not encode int value for `$value`. (param=$param)")
            }
            writeInt(converted)
        }
    }
    return this
}
