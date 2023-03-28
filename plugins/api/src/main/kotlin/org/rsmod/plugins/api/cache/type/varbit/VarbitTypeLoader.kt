package org.rsmod.plugins.api.cache.type.varbit

import io.netty.buffer.ByteBuf
import org.openrs2.buffer.readString
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.plugins.api.cache.Js5Archives
import org.rsmod.plugins.api.cache.Js5Configs
import org.rsmod.plugins.api.cache.type.ConfigType
import java.io.IOException

public object VarbitTypeLoader {

    public fun load(cache: Cache): List<VarbitType> {
        val types = mutableListOf<VarbitType>()
        val files = cache.list(Js5Archives.CONFIG, Js5Configs.VARBIT)
        files.forEach { file ->
            cache.read(Js5Archives.CONFIG, Js5Configs.VARBIT, file.id).use {
                types += readType(it, file.id)
            }
        }
        return types
    }

    public fun readType(buf: ByteBuf, id: Int): VarbitType {
        val builder = VarbitTypeBuilder().apply { this.id = id }
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
        builder: VarbitTypeBuilder,
        instruction: Int
    ): Unit = with(builder) {
        when (instruction) {
            1 -> {
                varp = buf.readUnsignedShort()
                lsb = buf.readUnsignedByte().toInt()
                msb = buf.readUnsignedByte().toInt()
            }
            ConfigType.TRANSMISSION_OPCODE -> transmit = true
            ConfigType.INTERNAL_NAME_OPCODE -> name = buf.readString()
            else -> throw IOException("Error unrecognised varp config code: $instruction")
        }
    }
}
