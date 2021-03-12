package org.rsmod.plugins.api.cache.type.item

import com.github.michaelbull.logging.InlineLogger
import javax.inject.Inject
import org.rsmod.game.cache.ConfigTypeLoader
import org.rsmod.game.cache.GameCache
import org.rsmod.game.model.item.type.ItemType
import org.rsmod.game.model.item.type.ItemTypeList
import io.guthix.buffer.readStringCP1252
import io.netty.buffer.ByteBuf
import org.rsmod.game.model.item.type.ItemTypeBuilder

private val logger = InlineLogger()
private const val ITEM_ARCHIVE = 2
private const val ITEM_GROUP = 10

class ItemTypeLoader @Inject constructor(
    private val cache: GameCache,
    private val types: ItemTypeList
) : ConfigTypeLoader {

    override fun load() {
        val files = cache.groups(ITEM_ARCHIVE, ITEM_GROUP)
        files.forEach { (file, data) ->
            val type = data.type(file)
            types.add(type)
        }
        logger.info { "Loaded ${types.size} item type files" }
    }

    private fun ByteBuf.type(id: Int): ItemType {
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
            1 -> buf.readUnsignedShort()
            2 -> name = buf.readStringCP1252()
            4 -> buf.readUnsignedShort()
            5 -> buf.readUnsignedShort()
            6 -> buf.readUnsignedShort()
            7 -> buf.readUnsignedShort()
            8 -> buf.readUnsignedShort()
            11 -> stacks = true
            12 -> cost = buf.readInt()
            16 -> members = true
            23 -> {
                buf.readUnsignedShort()
                buf.readUnsignedByte()
            }
            24 -> buf.readUnsignedShort()
            25 -> {
                buf.readUnsignedShort()
                buf.readUnsignedByte()
            }
            26 -> buf.readUnsignedShort()
            in 30 until 35 -> {
                if (defaultGroundOps) groundOptions = arrayOfNulls(5)
                val index = instruction - 30
                val option = buf.readStringCP1252()
                groundOptions[index] = if (option == "Hidden") null else option
            }
            in 35 until 40 -> {
                if (defaultInventoryOps) inventoryOptions = arrayOfNulls(5)
                val index = instruction - 35
                val option = buf.readStringCP1252()
                inventoryOptions[index] = option
            }
            40, 41 -> {
                val count = buf.readUnsignedByte().toInt()
                repeat(count) {
                    buf.readUnsignedShort()
                    buf.readUnsignedShort()
                }
            }
            42 -> buf.readByte()
            65 -> exchangeable = true
            78 -> buf.readUnsignedShort()
            79 -> buf.readUnsignedShort()
            90 -> buf.readUnsignedShort()
            91 -> buf.readUnsignedShort()
            92 -> buf.readUnsignedShort()
            93 -> buf.readUnsignedShort()
            95 -> buf.readUnsignedShort()
            97 -> noteLink = buf.readUnsignedShort()
            98 -> noteValue = buf.readUnsignedShort()
            in 100 until 110 -> {
                buf.readUnsignedShort()
                buf.readUnsignedShort()
            }
            110 -> buf.readUnsignedShort()
            111 -> buf.readUnsignedShort()
            112 -> buf.readUnsignedShort()
            113 -> buf.readByte()
            114 -> buf.readByte()
            115 -> teamCape = buf.readUnsignedByte().toInt()
            139 -> buf.readUnsignedShort()
            140 -> buf.readUnsignedShort()
            148 -> placeholderLink = buf.readUnsignedShort()
            149 -> placeholderValue = buf.readUnsignedShort()
            249 -> {
                val parameters = mutableMapOf<Int, Any>()
                val count = buf.readUnsignedByte().toInt()
                repeat(count) {
                    val readString = buf.readBoolean()
                    val key = buf.readUnsignedMedium()
                    if (readString) {
                        parameters[key] = buf.readStringCP1252()
                    } else {
                        parameters[key] = buf.readInt()
                    }
                }
                this.parameters = parameters
            }
        }
    }
}
