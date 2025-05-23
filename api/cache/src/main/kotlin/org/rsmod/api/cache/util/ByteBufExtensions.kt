package org.rsmod.api.cache.util

import io.netty.buffer.ByteBuf
import org.openrs2.buffer.readString
import org.openrs2.buffer.readUnsignedShortSmart
import org.openrs2.buffer.writeString
import org.openrs2.buffer.writeUnsignedShortSmart
import org.rsmod.game.type.literal.CacheVarLiteral
import org.rsmod.map.CoordGrid

public fun ByteBuf.encodeConfig(encode: ByteBuf.() -> Unit): ByteBuf = apply {
    encode(this)
    writeByte(0)
}

public fun ByteBuf.writeCoordGrid(coords: CoordGrid): ByteBuf {
    writeInt(coords.packed)
    return this
}

public fun ByteBuf.readCoordGrid(): CoordGrid = CoordGrid(readInt())

public fun ByteBuf.writeRawParams(params: Map<Int, Any>): ByteBuf {
    writeByte(params.size)
    for ((key, value) in params.entries) {
        when (value) {
            is String -> {
                writeBoolean(true)
                writeMedium(key)
                writeString(value)
            }

            is Int -> {
                writeBoolean(false)
                writeMedium(key)
                writeInt(value)
            }

            else -> error("Invalid param value: $value.")
        }
    }
    return this
}

public fun ByteBuf.readRawParams(): MutableMap<Int, Any> {
    val parameters = mutableMapOf<Int, Any>()
    val count = readUnsignedByte().toInt()
    repeat(count) {
        val isString = readBoolean()
        val key = readUnsignedMedium()
        if (isString) {
            parameters[key] = readString()
        } else {
            parameters[key] = readInt()
        }
    }
    return parameters
}

public fun ByteBuf.readIncrUnsignedShortSmart(): Int {
    var value = 0
    var curr = readUnsignedShortSmart()
    while (curr == 0x7FFF) {
        value += curr
        curr = readUnsignedShortSmart()
    }
    value += curr
    return value
}

public fun ByteBuf.writeNullableShort(value: Int?): ByteBuf {
    writeShort(value ?: 65535)
    return this
}

public fun ByteBuf.readUnsignedShortOrNull(): Int? {
    val value = readUnsignedShort()
    return if (value == 65535) null else value
}

public fun ByteBuf.writeNullableLargeSmart(value: Int?): ByteBuf =
    when {
        value == null -> writeShort(32767)
        value < Short.MAX_VALUE -> writeShort(value)
        else -> writeInt(value)
    }

// Also known as `gSmart2or4s`.
public fun ByteBuf.readNullableLargeSmart(): Int? =
    if (getByte(readerIndex()) < 0) {
        readInt() and Integer.MAX_VALUE
    } else {
        val result = readUnsignedShort()
        if (result == 32767) null else result
    }

public fun ByteBuf.writeSmallSmartPlusOne(value: Int): ByteBuf {
    when (value) {
        in 0 until 128 -> writeByte(value + 1)
        in 128 until 32768 -> writeShort(value + 32769)
        else -> throw IllegalArgumentException("`value` must be less than 32767: $value.")
    }
    return this
}

public fun ByteBuf.readUnsignedSmallSmartPlusOne(): Int {
    val peak = getUnsignedByte(readerIndex())
    return if (peak < 128) readUnsignedByte().toInt() - 1 else readUnsignedShort() - 32769
}

public fun ByteBuf.writeIntSmart(value: Int): ByteBuf {
    require(value > Short.MAX_VALUE)
    require(value < (Int.MAX_VALUE - Short.MAX_VALUE))
    val diff = value - Short.MAX_VALUE
    writeShort(0xFFFF)
    writeUnsignedShortSmart(diff)
    return this
}

public fun ByteBuf.writeUnsignedSmartInt(value: Int): ByteBuf =
    if (value > Short.MAX_VALUE) {
        writeIntSmart(value)
    } else {
        writeUnsignedShortSmart(value)
    }

public fun ByteBuf.toInlineBuf(): InlineByteBuf =
    if (hasArray()) {
        val bytes = array().copyOf(writerIndex())
        InlineByteBuf(bytes)
    } else {
        val bytes = ByteArray(writerIndex())
        readBytes(bytes)
        InlineByteBuf(bytes)
    }

public fun ByteBuf.readColumnValues(types: List<Int>): List<Any> {
    val groupCount = readUnsignedShortSmart()
    val allValues = ArrayList<Any>(groupCount * types.size)
    repeat(groupCount) {
        for (typeIndex in types.indices) {
            val type = types[typeIndex]
            val value = if (type == CacheVarLiteral.STRING.id) readString() else readInt()
            allValues += value
        }
    }
    return allValues
}

public fun ByteBuf.writeColumnValues(values: List<Any>, types: List<Int>) {
    val groupCount = if (types.isEmpty()) 0 else values.size / types.size
    writeUnsignedShortSmart(groupCount)
    for (i in values.indices) {
        val type = types[i % types.size]
        val value = values[i]
        if (type == CacheVarLiteral.STRING.id) {
            writeString(value as String)
        } else {
            writeInt(value as Int)
        }
    }
}
