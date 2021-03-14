package org.rsmod.plugins.api.cache.type.vars

import com.github.michaelbull.logging.InlineLogger
import javax.inject.Inject
import io.netty.buffer.ByteBuf
import java.io.IOException
import org.rsmod.game.cache.type.ConfigTypeLoader
import org.rsmod.game.cache.GameCache
import org.rsmod.game.model.vars.type.VarbitType
import org.rsmod.game.model.vars.type.VarbitTypeBuilder
import org.rsmod.game.model.vars.type.VarbitTypeList

private val logger = InlineLogger()
private const val VARBIT_ARCHIVE = 2
private const val VARBIT_GROUP = 14

class VarbitTypeLoader @Inject constructor(
    private val cache: GameCache,
    private val types: VarbitTypeList
) : ConfigTypeLoader {

    override fun load() {
        val files = cache.groups(VARBIT_ARCHIVE, VARBIT_GROUP)
        files.forEach { (file, data) ->
            val type = data.type(file)
            types.add(type)
        }
        logger.info { "Loaded ${types.size} varbit type files" }
    }

    private fun ByteBuf.type(id: Int): VarbitType {
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
            else -> throw IOException("Unhandled buffer opcode $instruction.")
        }
    }
}
