package org.rsmod.plugins.cache.config.param

import io.netty.buffer.ByteBuf
import org.openrs2.buffer.readString
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.plugins.cache.Js5Archives
import org.rsmod.plugins.cache.Js5Configs
import org.rsmod.plugins.cache.config.ConfigType
import java.io.IOException

public object ParamTypeLoader {

    public fun load(cache: Cache): List<ParamType<*>> {
        val types = mutableListOf<ParamType<*>>()
        val files = cache.list(Js5Archives.CONFIG, Js5Configs.PARAM)
        files.forEach { file ->
            cache.read(Js5Archives.CONFIG, Js5Configs.PARAM, file.id).use {
                types += readType(it, file.id)
            }
        }
        return types
    }

    public fun readType(buf: ByteBuf, id: Int): ParamType<*> {
        val builder = ParamTypeBuilder().apply { this.id = id }
        while (buf.isReadable) {
            val instruction = buf.readUnsignedByte().toInt()
            if (instruction == 0) {
                break
            }
            readBuffer(buf, builder, instruction)
        }
        return builder.build()
    }

    private fun readBuffer(
        buf: ByteBuf,
        builder: ParamTypeBuilder,
        instruction: Int
    ): Unit = with(builder) {
        when (instruction) {
            1 -> typeChar = buf.readUnsignedByte().toInt().toChar()
            2 -> defaultInt = buf.readInt()
            4 -> autoDisable = false
            5 -> defaultStr = buf.readString()
            ConfigType.TRANSMISSION_OPCODE -> transmit = true
            ConfigType.INTERNAL_NAME_OPCODE -> name = buf.readString()
            else -> throw IOException("Error unrecognised param config code: $instruction")
        }
    }
}
