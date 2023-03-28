package org.rsmod.plugins.cache.config.enums

import io.netty.buffer.ByteBuf
import org.openrs2.buffer.readString
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.plugins.cache.Js5Archives
import org.rsmod.plugins.cache.Js5Configs
import org.rsmod.plugins.cache.config.ConfigType
import java.io.IOException

public object EnumTypeLoader {

    public fun load(cache: Cache): List<EnumType<Any, Any>> {
        val types = mutableListOf<EnumType<Any, Any>>()
        val files = cache.list(Js5Archives.CONFIG, Js5Configs.ENUM)
        files.forEach { file ->
            cache.read(Js5Archives.CONFIG, Js5Configs.ENUM, file.id).use {
                types += readType(it, file.id)
            }
        }
        return types
    }

    public fun readType(buf: ByteBuf, id: Int): EnumType<Any, Any> {
        val builder = EnumTypeBuilder().apply { this.id = id }
        while (buf.isReadable) {
            val instruction = buf.readUnsignedByte().toInt()
            if (instruction == 0) {
                break
            }
            decodeType(buf, builder, instruction)
        }
        return builder.build()
    }

    private fun decodeType(
        buf: ByteBuf,
        builder: EnumTypeBuilder,
        instruction: Int
    ): Unit = with(builder) {
        when (instruction) {
            1 -> keyType = buf.readUnsignedByte().toInt().toChar()
            2 -> valType = buf.readUnsignedByte().toInt().toChar()
            3 -> defaultStr = buf.readString()
            4 -> defaultInt = buf.readInt()
            5 -> {
                check(size == 0)
                size = buf.readUnsignedShort()
                repeat(size) {
                    val key = buf.readInt()
                    val value = buf.readString()
                    strValues[key] = value
                }
            }
            6 -> {
                check(size == 0)
                size = buf.readUnsignedShort()
                repeat(size) {
                    val key = buf.readInt()
                    val value = buf.readInt()
                    intValues[key] = value
                }
            }
            ConfigType.TRANSMISSION_OPCODE -> transmit = true
            ConfigType.INTERNAL_NAME_OPCODE -> name = buf.readString()
            else -> throw IOException("Error unrecognised enum config code: $instruction")
        }
    }
}
