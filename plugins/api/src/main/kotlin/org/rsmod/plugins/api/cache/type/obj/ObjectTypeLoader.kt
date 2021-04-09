package org.rsmod.plugins.api.cache.type.obj

import com.github.michaelbull.logging.InlineLogger
import io.guthix.buffer.readStringCP1252
import io.netty.buffer.ByteBuf
import org.rsmod.game.cache.GameCache
import org.rsmod.game.cache.type.ConfigTypeLoader
import org.rsmod.game.model.obj.type.ObjectType
import org.rsmod.game.model.obj.type.ObjectTypeBuilder
import org.rsmod.game.model.obj.type.ObjectTypeList
import org.rsmod.plugins.api.cache.readParameters
import java.io.IOException
import javax.inject.Inject

private val logger = InlineLogger()

private const val OBJ_ARCHIVE = 2
private const val OBJ_GROUP = 6

class ObjectTypeLoader @Inject constructor(
    private val cache: GameCache,
    private val types: ObjectTypeList
) : ConfigTypeLoader {

    override fun load() {
        val files = cache.groups(OBJ_ARCHIVE, OBJ_GROUP)
        files.forEach { (file, data) ->
            val type = data.type(file)
            types.add(type)
        }
        logger.info { "Loaded ${types.size} object type files" }
    }

    private fun ByteBuf.type(id: Int): ObjectType {
        val builder = ObjectTypeBuilder().apply { this.id = id }
        while (isReadable) {
            val instruction = readUnsignedByte().toInt()
            if (instruction == 0) {
                break
            }
            builder.readBuffer(instruction, this)
        }
        return builder.setDefaultValues().build()
    }

    private fun ObjectTypeBuilder.readBuffer(instruction: Int, buf: ByteBuf) {
        when (instruction) {
            1 -> {
                val count = buf.readUnsignedByte().toInt()
                val models = IntArray(count)
                val types = IntArray(count)
                repeat(count) {
                    models[it] = buf.readUnsignedShort()
                    types[it] = buf.readUnsignedByte().toInt()
                }
                this.models = models.toTypedArray()
                this.modelTypes = types.toTypedArray()
            }
            2 -> name = buf.readStringCP1252()
            5 -> {
                val count = buf.readUnsignedByte().toInt()
                val models = IntArray(count)
                repeat(count) {
                    models[it] = buf.readUnsignedShort()
                }
                this.models = models.toTypedArray()
                this.modelTypes = emptyArray()
            }
            14 -> width = buf.readUnsignedByte().toInt()
            15 -> height = buf.readUnsignedByte().toInt()
            17 -> {
                clipType = 0
                blockPath = false
            }
            18 -> blockProjectile = false
            19 -> interactType = buf.readUnsignedByte().toInt()
            21 -> contouredGround = 0
            22 -> nonFlatShading = true
            23 -> clippedModel = true
            24 -> {
                animation = buf.readUnsignedShort()
                if (animation == 65535) animation = -1
            }
            27 -> clipType = 1
            28 -> decorDisplacement = buf.readUnsignedByte().toInt()
            29 -> ambient = buf.readByte().toInt()
            in 30 until 35 -> {
                if (defaultOptions) options = arrayOfNulls(5)
                val index = instruction - 30
                val option = buf.readStringCP1252()
                options[index] = if (option == "Hidden") null else option
            }
            39 -> contrast = buf.readByte() * 25
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
            62 -> rotated = true
            64 -> clipped = false
            65 -> modelSizeX = buf.readUnsignedShort()
            66 -> modelSizeHeight = buf.readUnsignedShort()
            67 -> modelSizeY = buf.readUnsignedShort()
            68 -> mapSceneId = buf.readUnsignedShort()
            69 -> clipMask = buf.readUnsignedByte().toInt()
            70 -> offsetX = buf.readShort().toInt()
            71 -> offsetHeight = buf.readShort().toInt()
            72 -> offsetY = buf.readShort().toInt()
            73 -> obstruct = true
            74 -> hollow = true
            75 -> supportItems = buf.readUnsignedByte().toInt()
            77, 92 -> {
                varbit = buf.readUnsignedShort()
                if (varbit == 65535) varbit = -1

                varp = buf.readUnsignedShort()
                if (varp == 65535) varp = -1

                if (instruction == 92) {
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
            78 -> {
                ambientSoundId = buf.readUnsignedShort()
                ambientSoundRadius = buf.readUnsignedByte().toInt()
            }
            79 -> {
                anInt3426 = buf.readUnsignedShort()
                anInt3427 = buf.readUnsignedShort()
                ambientSoundRadius = buf.readUnsignedByte().toInt()
                val count = buf.readUnsignedByte().toInt()
                val array = IntArray(count)
                repeat(count) {
                    array[it] = buf.readUnsignedShort()
                }
                anIntArray3428 = array.toTypedArray()
            }
            81 -> contouredGround = buf.readUnsignedByte().toInt() * 256
            82 -> mapIconId = buf.readUnsignedShort()
            89 -> aBoolean3429 = false
            249 -> parameters = buf.readParameters()
            else -> throw IOException("Error unrecognised object config code: $instruction")
        }
    }

    private fun ObjectTypeBuilder.setDefaultValues(): ObjectTypeBuilder {
        if (interactType == -1) {
            interactType = 0
            if (models.isNotEmpty() && (modelTypes.isEmpty() || modelTypes[0] == 10)) {
                interactType = 1
            }
            if (options.firstOrNull() != null) {
                interactType = 1
            }
        }
        if (supportItems == -1) {
            supportItems = if (clipType != 0) 1 else 0
        }
        return this
    }
}
