package org.rsmod.plugins.api.cache.type.npc

import io.netty.buffer.ByteBuf
import org.openrs2.buffer.readIntSmart
import org.openrs2.buffer.readShortSmart
import org.openrs2.buffer.readString
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.plugins.api.cache.type.param.ParamTypeList
import org.rsmod.plugins.api.cache.type.readParams
import java.io.IOException

private const val CONFIG_ARCHIVE = 2
private const val NPC_GROUP = 9

public object NpcTypeLoader {

    public fun load(cache: Cache, params: ParamTypeList): List<NpcType> {
        val types = mutableListOf<NpcType>()
        val files = cache.list(CONFIG_ARCHIVE, NPC_GROUP)
        files.forEach { file ->
            cache.read(CONFIG_ARCHIVE, NPC_GROUP, file.id).use {
                types += readType(it, file.id, params)
            }
        }
        return types
    }

    public fun readType(buf: ByteBuf, id: Int, params: ParamTypeList): NpcType {
        val builder = NpcTypeBuilder().apply { this.id = id }
        while (buf.isReadable) {
            val instruction = buf.readUnsignedByte().toInt()
            if (instruction == 0) {
                break
            }
            readBuffer(buf, builder, instruction, params)
        }
        return builder.build()
    }

    private fun readBuffer(
        buf: ByteBuf,
        builder: NpcTypeBuilder,
        instruction: Int,
        paramTypes: ParamTypeList
    ): Unit = with(builder) {
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
            102 -> {
                val initialBits = buf.readUnsignedByte().toInt()
                var bits = initialBits
                var count = 0
                while (bits != 0) {
                    count++
                    bits = bits shr 1
                }
                val groups = mutableListOf<Int>()
                val indexes = mutableListOf<Int>()
                repeat(count) { i ->
                    if ((initialBits and 0x1 shl i) == 0) {
                        groups += -1
                        indexes += -1
                        return@repeat
                    }
                    groups += buf.readIntSmart()
                    indexes += buf.readShortSmart()
                }
                headIconGroups = groups.toTypedArray()
                headIconIndexes = indexes.toTypedArray()
            }
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
            249 -> params = buf.readParams(paramTypes)
            else -> throw IOException("Error unrecognised npc config code: $instruction")
        }
    }
}
