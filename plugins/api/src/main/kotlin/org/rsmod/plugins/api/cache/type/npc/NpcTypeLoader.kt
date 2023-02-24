package org.rsmod.plugins.api.cache.type.npc

import io.netty.buffer.ByteBuf
import org.openrs2.buffer.readString
import org.openrs2.cache.Cache
import org.rsmod.plugins.api.cache.type.readStruct
import java.io.IOException
import javax.inject.Inject

private const val CONFIG_ARCHIVE = 2
private const val NPC_GROUP = 9

public class NpcTypeLoader @Inject constructor(private val cache: Cache) {

    public fun load(): List<NpcType> {
        val types = mutableListOf<NpcType>()
        val files = cache.list(CONFIG_ARCHIVE, NPC_GROUP)
        files.forEach { file ->
            val data = cache.read(CONFIG_ARCHIVE, NPC_GROUP, file.id)
            types += data.readType(file.id)
        }
        return types
    }

    private fun ByteBuf.readType(id: Int): NpcType {
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
            2 -> name = buf.readString()
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
            18 -> category = buf.readUnsignedShort()
            in 30 until 35 -> {
                if (defaultOptions) options = arrayOfNulls(5)
                val index = instruction - 30
                val option = buf.readString()
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
                transforms = Array(count + 1) { 0 }
                for (i in 0..count) {
                    val transformId = buf.readUnsignedShort()
                    transforms[i] = if (transformId == 65535) -1 else transformId
                }
            }
            107 -> interact = false
            109 -> clickable = false
            111 -> isPet = true
            114 -> buf.readUnsignedShort()
            115 -> {
                buf.readUnsignedShort()
                buf.readUnsignedShort()
                buf.readUnsignedShort()
                buf.readUnsignedShort()
            }
            116 -> buf.readUnsignedShort()
            117 -> {
                buf.readUnsignedShort()
                buf.readUnsignedShort()
                buf.readUnsignedShort()
                buf.readUnsignedShort()
            }
            249 -> parameters = buf.readStruct()
            else -> throw IOException("Error unrecognised npc config code: $instruction")
        }
    }
}
