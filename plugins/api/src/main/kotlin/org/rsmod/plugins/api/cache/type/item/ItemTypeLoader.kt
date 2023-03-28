package org.rsmod.plugins.api.cache.type.item

import io.netty.buffer.ByteBuf
import org.openrs2.buffer.readString
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.plugins.api.cache.Js5Archives
import org.rsmod.plugins.api.cache.Js5Configs
import org.rsmod.plugins.api.cache.type.ConfigType
import org.rsmod.plugins.api.cache.type.param.ParamTypeList
import org.rsmod.plugins.api.cache.type.readParams
import java.io.IOException

public object ItemTypeLoader {

    public fun load(cache: Cache, params: ParamTypeList): List<ItemType> {
        val types = mutableListOf<ItemType>()
        val files = cache.list(Js5Archives.CONFIG, Js5Configs.OBJ)
        files.forEach { file ->
            cache.read(Js5Archives.CONFIG, Js5Configs.OBJ, file.id).use {
                types += readType(it, file.id, params)
            }
        }
        return types
    }

    public fun readType(buf: ByteBuf, id: Int, params: ParamTypeList): ItemType {
        val builder = ItemTypeBuilder().apply { this.id = id }
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
        builder: ItemTypeBuilder,
        instruction: Int,
        paramTypes: ParamTypeList
    ): Unit = with(builder) {
        when (instruction) {
            1 -> model = buf.readUnsignedShort()
            2 -> name = buf.readString()
            4 -> zoom2d = buf.readUnsignedShort()
            5 -> xan2d = buf.readUnsignedShort()
            6 -> yan2d = buf.readUnsignedShort()
            7 -> xOff2d = buf.readShort().toInt()
            8 -> yOff2d = buf.readShort().toInt()
            11 -> stacks = true
            12 -> cost = buf.readInt()
            13 -> wearPos1 = buf.readByte().toInt()
            14 -> wearPos2 = buf.readByte().toInt()
            16 -> members = true
            23 -> {
                maleModel0 = buf.readUnsignedShort()
                maleModelOffset = buf.readUnsignedByte().toInt()
            }

            24 -> maleModel1 = buf.readUnsignedShort()
            25 -> {
                femaleModel0 = buf.readUnsignedShort()
                femaleModelOffset = buf.readUnsignedByte().toInt()
            }

            26 -> femaleModel1 = buf.readUnsignedShort()
            27 -> wearPos3 = buf.readByte().toInt()
            in 30 until 35 -> {
                if (defaultGroundOps) groundOptions = arrayOfNulls(5)
                val index = instruction - 30
                val option = buf.readString()
                groundOptions[index] = if (option == "Hidden") null else option
            }

            in 35 until 40 -> {
                if (defaultInventoryOps) inventoryOptions = arrayOfNulls(5)
                val index = instruction - 35
                val option = buf.readString()
                inventoryOptions[index] = option
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
                    recolorSrc = src
                    recolorDest = dest
                } else {
                    retextureSrc = src
                    retextureDest = dest
                }
            }

            42 -> dropOptionIndex = buf.readByte().toInt()
            65 -> exchangeable = true
            75 -> weight = buf.readShort().toInt()
            78 -> maleModel2 = buf.readUnsignedShort()
            79 -> femaleModel2 = buf.readUnsignedShort()
            90 -> maleHeadModel0 = buf.readUnsignedShort()
            91 -> femaleHeadModel0 = buf.readUnsignedShort()
            92 -> maleHeadModel1 = buf.readUnsignedShort()
            93 -> femaleHeadModel1 = buf.readUnsignedShort()
            94 -> {
                if (defaultCategories) categories = mutableSetOf()
                categories += buf.readUnsignedShort()
            }

            95 -> zan2d = buf.readUnsignedShort()
            97 -> noteLink = buf.readUnsignedShort()
            98 -> noteModel = buf.readUnsignedShort()
            in 100 until 110 -> {
                if (countItem.isEmpty()) {
                    countItem = IntArray(10)
                    countCo = IntArray(10)
                }
                val index = instruction - 100
                countItem[index] = buf.readUnsignedShort()
                countCo[index] = buf.readUnsignedShort()
            }

            110 -> resizeX = buf.readUnsignedShort()
            111 -> resizeY = buf.readUnsignedShort()
            112 -> resizeZ = buf.readUnsignedShort()
            113 -> ambient = buf.readByte().toInt()
            114 -> contrast = buf.readByte().toInt()
            115 -> team = buf.readUnsignedByte().toInt()
            139 -> boughtLink = buf.readUnsignedShort()
            140 -> boughtValue = buf.readUnsignedShort()
            148 -> placeholderLink = buf.readUnsignedShort()
            149 -> placeholderModel = buf.readUnsignedShort()
            249 -> params = buf.readParams(paramTypes)
            ConfigType.INTERNAL_NAME_OPCODE -> name = buf.readString()
            else -> throw IOException("Error unrecognised item config code: $instruction")
        }
    }
}
