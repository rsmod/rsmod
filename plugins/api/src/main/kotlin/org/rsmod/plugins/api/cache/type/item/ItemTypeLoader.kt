package org.rsmod.plugins.api.cache.type.item

import io.netty.buffer.ByteBuf
import org.openrs2.buffer.readString
import org.openrs2.cache.Cache
import org.rsmod.plugins.api.cache.type.readStruct
import java.io.IOException
import javax.inject.Inject

private const val CONFIG_ARCHIVE = 2
private const val ITEM_GROUP = 10

public class ItemTypeLoader @Inject constructor(private val cache: Cache) {

    public fun load(): List<ItemType> {
        val types = mutableListOf<ItemType>()
        val files = cache.list(CONFIG_ARCHIVE, ITEM_GROUP)
        files.forEach { file ->
            val data = cache.read(CONFIG_ARCHIVE, ITEM_GROUP, file.id)
            types += data.readType(file.id)
        }
        return types
    }

    private fun ByteBuf.readType(id: Int): ItemType {
        val builder = ItemTypeBuilder().apply { this.id = id }
        while (isReadable) {
            val instruction = readUnsignedByte().toInt()
            if (instruction == 0) {
                break
            }
            builder.readBuffer(instruction, this)
        }
        return builder.build()
    }

    private fun ItemTypeBuilder.readBuffer(instruction: Int, buf: ByteBuf) {
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
            115 -> teamCape = buf.readUnsignedByte().toInt()
            139 -> boughtLink = buf.readUnsignedShort()
            140 -> boughtValue = buf.readUnsignedShort()
            148 -> placeholderLink = buf.readUnsignedShort()
            149 -> placeholderModel = buf.readUnsignedShort()
            249 -> parameters = buf.readStruct()
            else -> throw IOException("Error unrecognised item config code: $instruction")
        }
    }
}
