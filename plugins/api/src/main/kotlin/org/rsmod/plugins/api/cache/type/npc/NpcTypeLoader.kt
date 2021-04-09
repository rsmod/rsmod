package org.rsmod.plugins.api.cache.type.npc

import com.github.michaelbull.logging.InlineLogger
import io.guthix.buffer.readStringCP1252
import io.netty.buffer.ByteBuf
import org.rsmod.game.cache.GameCache
import org.rsmod.game.cache.type.ConfigTypeLoader
import org.rsmod.game.model.npc.type.NpcType
import org.rsmod.game.model.npc.type.NpcTypeBuilder
import org.rsmod.game.model.npc.type.NpcTypeList
import org.rsmod.plugins.api.cache.readParameters
import java.io.IOException
import javax.inject.Inject

private val logger = InlineLogger()

private const val NPC_ARCHIVE = 2
private const val NPC_GROUP = 9

class NpcTypeLoader @Inject constructor(
    private val cache: GameCache,
    private val types: NpcTypeList
) : ConfigTypeLoader {

    override fun load() {
        val files = cache.groups(NPC_ARCHIVE, NPC_GROUP)
        files.forEach { (file, data) ->
            val type = data.type(file)
            types.add(type)
        }
        logger.info { "Loaded ${types.size} npc type files" }
    }

    private fun ByteBuf.type(id: Int): NpcType {
        val builder = NpcTypeBuilder().apply { this.id = id }
        while (isReadable) {
            val instruction = readUnsignedByte().toInt()
            if (instruction == 0) {
                break
            }
            builder.readBuffer(instruction, this)
        }
        return builder.build()
    }

    private fun NpcTypeBuilder.readBuffer(instruction: Int, buf: ByteBuf) {
        when (instruction) {
            1 -> {
                val count = buf.readUnsignedByte().toInt()
                val models = IntArray(count)
                repeat(count) {
                    models[it] = buf.readUnsignedShort()
                }
                this.models = models.toTypedArray()
            }
            2 -> name = buf.readStringCP1252()
            12 -> size = buf.readUnsignedByte().toInt()
            13 -> readyAnim = buf.readUnsignedShort()
            14 -> walkAnim = buf.readUnsignedShort()
            15 -> turnLeftAnim = buf.readUnsignedShort()
            16 -> turnRightAnim = buf.readUnsignedShort()
            17 -> {
                walkAnim = buf.readUnsignedShort()
                walkBackAnim = buf.readUnsignedShort()
                walkLeftAnim = buf.readUnsignedShort()
                walkRightAnim = buf.readUnsignedShort()
            }
            in 30 until 35 -> {
                if (defaultOptions) options = arrayOfNulls(5)
                val index = instruction - 30
                val option = buf.readStringCP1252()
                options[index] = if (option == "Hidden") null else option
            }
            40, 41 -> {
                val count = buf.readUnsignedByte().toInt()
                val src = IntArray(count)
                val dest = IntArray(count)
                repeat(count) {
                    src[it] = buf.readUnsignedShort()
                    dest[it] = buf.readUnsignedShort()
                }
                if (instruction == 40) {
                    recolorSrc = src.toTypedArray()
                    recolorDest = dest.toTypedArray()
                } else {
                    retextureSrc = src.toTypedArray()
                    retextureDest = dest.toTypedArray()
                }
            }
            60 -> {
                val count = buf.readUnsignedByte().toInt()
                val models = IntArray(count)
                repeat(count) {
                    models[it] = buf.readUnsignedShort()
                }
                headModels = models.toTypedArray()
            }
            93 -> minimapVisible = false
            95 -> level = buf.readUnsignedShort()
            97 -> resizeX = buf.readUnsignedShort()
            98 -> resizeY = buf.readUnsignedShort()
            99 -> renderPriority = true
            100 -> ambient = buf.readByte().toInt()
            101 -> contrast = buf.readByte() * 5
            102 -> headIcon = buf.readUnsignedShort()
            103 -> rotation = buf.readUnsignedShort()
            106, 118 -> {
                varbit = buf.readUnsignedShort()
                if (varbit == 65535) varbit = -1

                varp = buf.readUnsignedShort()
                if (varp == 65535) varp = -1

                if (instruction == 118) {
                    defaultTransform = buf.readUnsignedShort()
                    if (defaultTransform == 65535) defaultTransform = -1
                }

                val count = buf.readUnsignedByte().toInt()
                transforms = Array(count + 2) { 0 }
                for (i in 0..count) {
                    val transformId = buf.readUnsignedShort()
                    transforms[i] = if (transformId == 65535) -1 else transformId
                }
                transforms[count + 1] = defaultTransform
            }
            107 -> interact = false
            109 -> clickable = false
            111 -> aBoolean3532 = true
            249 -> parameters = buf.readParameters()
            else -> throw IOException("Error unrecognised npc config code: $instruction")
        }
    }
}
