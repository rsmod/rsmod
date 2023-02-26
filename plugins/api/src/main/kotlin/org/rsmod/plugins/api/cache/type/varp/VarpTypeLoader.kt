package org.rsmod.plugins.api.cache.type.varp

import io.netty.buffer.ByteBuf
import org.openrs2.buffer.readString
import org.openrs2.cache.Cache
import org.rsmod.plugins.api.cache.build.game.GameCache
import org.rsmod.plugins.api.cache.type.ConfigType
import java.io.IOException
import javax.inject.Inject

public class VarpTypeLoader @Inject constructor(
    @GameCache private val cache: Cache
) {

    public fun load(): List<VarpType> {
        val types = mutableListOf<VarpType>()
        val files = cache.list(CONFIG_ARCHIVE, VARP_GROUP)
        files.forEach { file ->
            val data = cache.read(CONFIG_ARCHIVE, VARP_GROUP, file.id)
            types += readType(data, file.id)
        }
        return types
    }

    public companion object {

        private const val CONFIG_ARCHIVE = 2
        private const val VARP_GROUP = 16

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
}
