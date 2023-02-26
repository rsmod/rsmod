package org.rsmod.plugins.api.cache.type.item

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.openrs2.buffer.writeString
import org.openrs2.cache.Cache
import org.rsmod.plugins.api.cache.type.ConfigType
import org.rsmod.plugins.api.cache.type.param.ParamTypeList
import org.rsmod.plugins.api.cache.type.writeParams

public object ItemTypePacker {

    private const val CONFIG_ARCHIVE = 2
    private const val ITEM_GROUP = 10

    public fun pack(
        cache: Cache,
        types: Iterable<ItemType>,
        params: ParamTypeList,
        isJs5: Boolean
    ): List<ItemType> {
        val buf = Unpooled.buffer()
        val packed = mutableListOf<ItemType>()
        types.forEach { type ->
            buf.clear().writeType(type, params, isJs5)
            val oldData = if (cache.exists(CONFIG_ARCHIVE, ITEM_GROUP, type.id)) {
                cache.read(CONFIG_ARCHIVE, ITEM_GROUP, type.id)
            } else {
                null
            }
            if (buf == oldData) return@forEach
            cache.write(CONFIG_ARCHIVE, ITEM_GROUP, type.id, buf)
            packed += type
        }
        return packed
    }

    private fun ByteBuf.writeType(type: ItemType, params: ParamTypeList, isJs5: Boolean) {
        if (type.model != 0) {
            writeByte(1)
            writeShort(type.model)
        }
        if (type.name != "null") {
            writeByte(2)
            writeString(type.name)
        }
        if (type.zoom2d != 2000) {
            writeByte(4)
            writeShort(type.zoom2d)
        }
        if (type.xan2d != 0) {
            writeByte(5)
            writeShort(type.xan2d)
        }
        if (type.yan2d != 0) {
            writeByte(6)
            writeShort(type.yan2d)
        }
        if (type.xoff2d != 0) {
            writeByte(7)
            writeShort(type.xoff2d)
        }
        if (type.yoff2d != 0) {
            writeByte(8)
            writeShort(type.yoff2d)
        }
        if (type.stacks) {
            writeByte(11)
        }
        if (type.cost != 1) {
            writeByte(12)
            writeInt(type.cost)
        }
        if (type.wearPos1 != -1) {
            writeByte(13)
            writeByte(type.wearPos1)
        }
        if (type.wearPos2 != -1) {
            writeByte(14)
            writeByte(type.wearPos2)
        }
        if (type.members) {
            writeByte(16)
        }
        if (type.maleModel0 != 0) {
            writeByte(23)
            writeShort(type.maleModel0)
            writeByte(type.maleModelOffset)
        }
        if (type.maleModel1 != 0) {
            writeByte(24)
            writeShort(type.maleModel1)
        }
        if (type.femaleModel0 != 0) {
            writeByte(25)
            writeShort(type.femaleModel0)
            writeByte(type.femaleModelOffset)
        }
        if (type.femaleModel1 != 0) {
            writeByte(26)
            writeShort(type.femaleModel1)
        }
        if (type.wearPos3 != -1) {
            writeByte(27)
            writeByte(type.wearPos3)
        }
        type.groundOptions.forEachIndexed { i, str ->
            if (str != null && str != "Hidden" && str != "Take") {
                writeByte(30 + i)
                writeString(str)
            }
        }
        type.inventoryOptions.forEachIndexed { i, str ->
            if (str != null && str != "Hidden" && str != "Drop") {
                writeByte(35 + i)
                writeString(str)
            }
        }
        if (type.recolorSrc.isNotEmpty()) {
            writeByte(40)
            writeByte(type.recolorSrc.size)
            for (i in type.recolorSrc.indices) {
                writeShort(type.recolorSrc[i])
                writeShort(type.recolorDest[i])
            }
        }
        if (type.retextureSrc.isNotEmpty()) {
            writeByte(41)
            writeByte(type.retextureSrc.size)
            for (i in type.retextureSrc.indices) {
                writeShort(type.retextureSrc[i])
                writeShort(type.retextureDest[i])
            }
        }
        if (type.dropOptionIndex != -2) {
            writeByte(42)
            writeByte(type.dropOptionIndex)
        }
        if (type.exchangeable) {
            writeByte(65)
        }
        if (type.weight != 0) {
            writeByte(75)
            writeShort(type.weight)
        }
        if (type.maleModel2 != 0) {
            writeByte(78)
            writeShort(type.maleModel2)
        }
        if (type.femaleModel2 != 0) {
            writeByte(79)
            writeShort(type.femaleModel2)
        }
        if (type.maleHeadModel0 != 0) {
            writeByte(90)
            writeShort(type.maleHeadModel0)
        }
        if (type.femaleHeadModel0 != 0) {
            writeByte(91)
            writeShort(type.femaleHeadModel0)
        }
        if (type.maleHeadModel1 != 0) {
            writeByte(92)
            writeShort(type.maleHeadModel1)
        }
        if (type.femaleHeadModel1 != 0) {
            writeByte(93)
            writeShort(type.femaleHeadModel1)
        }
        type.categories.forEach { category ->
            writeByte(94)
            writeShort(category)
        }
        if (type.zan2d != 0) {
            writeByte(95)
            writeShort(type.zan2d)
        }
        if (type.noteLink != 0) {
            writeByte(97)
            writeShort(type.noteLink)
        }
        if (type.noteModel != 0) {
            writeByte(98)
            writeShort(type.noteModel)
        }
        type.countItem.forEachIndexed { i, obj ->
            val countCo = type.countCo[i]
            writeByte(100 + i)
            writeShort(obj)
            writeShort(countCo)
        }
        if (type.resizeX != 128) {
            writeByte(110)
            writeShort(type.resizeX)
        }
        if (type.resizeY != 128) {
            writeByte(111)
            writeShort(type.resizeY)
        }
        if (type.resizeZ != 128) {
            writeByte(112)
            writeShort(type.resizeZ)
        }
        if (type.ambient != 0) {
            writeByte(113)
            writeByte(type.ambient)
        }
        if (type.contrast != 0) {
            writeByte(114)
            writeByte(type.contrast)
        }
        if (type.team != 0) {
            writeByte(115)
            writeByte(type.team)
        }
        if (type.boughtLink != 0) {
            writeByte(139)
            writeShort(type.boughtLink)
        }
        if (type.boughtValue != 0) {
            writeByte(140)
            writeShort(type.boughtValue)
        }
        if (type.placeholderLink != 0) {
            writeByte(148)
            writeShort(type.placeholderLink)
        }
        if (type.placeholderModel != 0) {
            writeByte(149)
            writeShort(type.placeholderModel)
        }
        if (type.params != null) {
            writeByte(249)
            writeParams(type.params, params)
        }
        if (!isJs5) {
            type.internalName?.let {
                writeByte(ConfigType.INTERNAL_NAME_OPCODE)
                writeString(it)
            }
        }
        writeByte(0)
    }
}
