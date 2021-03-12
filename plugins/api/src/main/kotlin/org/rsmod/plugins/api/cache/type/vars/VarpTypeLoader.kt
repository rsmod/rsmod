package org.rsmod.plugins.api.cache.type.vars

import com.github.michaelbull.logging.InlineLogger
import javax.inject.Inject
import io.netty.buffer.ByteBuf
import java.io.IOException
import org.rsmod.game.cache.ConfigTypeLoader
import org.rsmod.game.cache.GameCache
import org.rsmod.game.model.vars.type.VarpType
import org.rsmod.game.model.vars.type.VarpTypeBuilder
import org.rsmod.game.model.vars.type.VarpTypeList

private val logger = InlineLogger()
private const val VARP_ARCHIVE = 2
private const val VARP_GROUP = 16

class VarpTypeLoader @Inject constructor(
    private val cache: GameCache,
    private val types: VarpTypeList
) : ConfigTypeLoader {

    override fun load() {
        val files = cache.groups(VARP_ARCHIVE, VARP_GROUP)
        files.forEach { (file, data) ->
            val type = data.type(file)
            types.add(type)
        }
        logger.info { "Loaded ${types.size} varp type files" }
    }

    private fun ByteBuf.type(id: Int): VarpType {
        val builder = VarpTypeBuilder().apply { this.id = id }
        while (isReadable) {
            val instruction = readUnsignedByte().toInt()
            if (instruction == 0) {
                break
            }
            builder.readBuffer(instruction, this)
        }
        return builder.build()
    }

    private fun VarpTypeBuilder.readBuffer(instruction: Int, buf: ByteBuf) {
        when (instruction) {
            5 -> type = buf.readUnsignedShort()
            else -> throw IOException("Unhandled buffer opcode $instruction.")
        }
    }
}
