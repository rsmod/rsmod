package org.rsmod.plugins.api.cache.type.obj

import io.netty.buffer.ByteBuf
import org.openrs2.buffer.readString
import org.openrs2.cache.Cache
import org.rsmod.plugins.api.cache.readStruct
import java.io.IOException
import javax.inject.Inject

private const val CONFIG_ARCHIVE = 2
private const val OBJECT_GROUP = 6

class ObjectTypeLoader @Inject constructor(private val cache: Cache) {

    fun load(): List<ObjectType> {
        val types = mutableListOf<ObjectType>()
        val files = cache.list(CONFIG_ARCHIVE, OBJECT_GROUP)
        files.forEach { file ->
            val data = cache.read(CONFIG_ARCHIVE, OBJECT_GROUP, file.id)
            types += data.readType(file.id)
        }
        return types
    }

    private fun ByteBuf.readType(id: Int): ObjectType {
        val builder = ObjectTypeBuilder().apply { this.id = id }
        while (isReadable) {
            val instruction = readUnsignedByte().toInt()
            if (instruction == 0) {
                break
            }
            builder.readBuffer(instruction, this)
        }
        return builder.build()
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
            2 -> name = buf.readString()
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
                val option = buf.readString()
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
            61 -> category = buf.readUnsignedShort()
            62 -> rotated = true
            64 -> clipped = false
            65 -> resizeX = buf.readUnsignedShort()
            66 -> resizeHeight = buf.readUnsignedShort()
            67 -> resizeY = buf.readUnsignedShort()
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
                transforms = Array(count + 1) { 0 }
                for (i in 0..count) {
                    val transformId = buf.readUnsignedShort()
                    transforms[i] = if (transformId == 65535) -1 else transformId
                }
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
            249 -> parameters = buf.readStruct()
            else -> throw IOException("Error unrecognised object config code: $instruction")
        }
    }
}
