package org.rsmod.plugins.api.cache.type.varbit

import io.netty.buffer.ByteBuf
import org.openrs2.buffer.readString
import org.openrs2.cache.Cache
import org.rsmod.plugins.api.cache.build.game.GameCache
import org.rsmod.plugins.api.cache.type.ConfigType
import java.io.IOException
import javax.inject.Inject

public class VarbitTypeLoader @Inject constructor(
    @GameCache private val cache: Cache
) {

    public fun load(): List<VarbitType> {
        val types = mutableListOf<VarbitType>()
        val files = cache.list(CONFIG_ARCHIVE, VARBIT_GROUP)
        files.forEach { file ->
            val data = cache.read(CONFIG_ARCHIVE, VARBIT_GROUP, file.id)
            types += readType(data, file.id)
        }
        return types
    }

    public companion object {

        private const val CONFIG_ARCHIVE = 2
        private const val VARBIT_GROUP = 14

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
}
