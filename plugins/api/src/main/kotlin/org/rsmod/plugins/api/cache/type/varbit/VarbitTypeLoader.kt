package org.rsmod.plugins.api.cache.type.varbit

import io.netty.buffer.ByteBuf
import org.openrs2.cache.Cache
import org.rsmod.plugins.api.cache.game.GameCache
import java.io.IOException
import javax.inject.Inject

private const val CONFIG_ARCHIVE = 2
private const val VARBIT_GROUP = 14

public class VarbitTypeLoader @Inject constructor(
    @GameCache private val cache: Cache
) {

    public fun load(): List<VarbitType> {
        val types = mutableListOf<VarbitType>()
        val files = cache.list(CONFIG_ARCHIVE, VARBIT_GROUP)
        files.forEach { file ->
            val data = cache.read(CONFIG_ARCHIVE, VARBIT_GROUP, file.id)
            types += data.readType(file.id)
        }
        return types
    }

    private fun ByteBuf.readType(id: Int): VarbitType {
        val builder = VarbitTypeBuilder().apply { this.id = id }
        while (isReadable) {
            val instruction = readUnsignedByte().toInt()
            if (instruction == 0) {
                break
            }
            builder.readBuffer(instruction, this)
        }
        return builder.build()
    }

    private fun VarbitTypeBuilder.readBuffer(instruction: Int, buf: ByteBuf) {
        when (instruction) {
            1 -> {
                varp = buf.readUnsignedShort()
                lsb = buf.readUnsignedByte().toInt()
                msb = buf.readUnsignedByte().toInt()
            }
            else -> throw IOException("Error unrecognised varp config code: $instruction")
        }
    }
}
