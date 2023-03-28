package org.rsmod.plugins.cache.config.varp

import io.netty.buffer.ByteBuf
import org.openrs2.buffer.readString
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.plugins.cache.Js5Archives
import org.rsmod.plugins.cache.Js5Configs
import org.rsmod.plugins.cache.config.ConfigType
import java.io.IOException

public object VarpTypeLoader {

    public fun load(cache: Cache): List<VarpType> {
        val types = mutableListOf<VarpType>()
        val files = cache.list(Js5Archives.CONFIG, Js5Configs.VARPLAYER)
        files.forEach { file ->
            cache.read(Js5Archives.CONFIG, Js5Configs.VARPLAYER, file.id).use {
                types += readType(it, file.id)
            }
        }
        return types
    }

    public fun readType(buf: ByteBuf, id: Int): VarpType {
        val builder = VarpTypeBuilder().apply { this.id = id }
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
        builder: VarpTypeBuilder,
        instruction: Int
    ): Unit = with(builder) {
        when (instruction) {
            5 -> clientCode = buf.readUnsignedShort()
            ConfigType.TRANSMISSION_OPCODE -> transmit = true
            ConfigType.INTERNAL_NAME_OPCODE -> name = buf.readString()
            else -> throw IOException("Error unrecognised varp config code: $instruction")
        }
    }
}
