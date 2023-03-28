package org.rsmod.plugins.cache.config.item

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.openrs2.buffer.writeString
import org.openrs2.cache.Cache
import org.rsmod.plugins.cache.config.ConfigType
import org.rsmod.plugins.cache.config.param.ParamTypeList
import org.rsmod.plugins.cache.config.writeParams

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
            writeType(buf.clear(), type, params, isJs5)
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

    public fun writeType(buf: ByteBuf, type: ItemType, params: ParamTypeList, isJs5: Boolean) {
        if (type.model != 0) {
            buf.writeByte(1)
            buf.writeShort(type.model)
        }
        if (type.name != "null") {
            buf.writeByte(2)
            buf.writeString(type.name)
        }
        if (type.zoom2d != 2000) {
            buf.writeByte(4)
            buf.writeShort(type.zoom2d)
        }
        if (type.xan2d != 0) {
            buf.writeByte(5)
            buf.writeShort(type.xan2d)
        }
        if (type.yan2d != 0) {
            buf.writeByte(6)
            buf.writeShort(type.yan2d)
        }
        if (type.xoff2d != 0) {
            buf.writeByte(7)
            buf.writeShort(type.xoff2d)
        }
        if (type.yoff2d != 0) {
            buf.writeByte(8)
            buf.writeShort(type.yoff2d)
        }
        if (type.stacks) {
            buf.writeByte(11)
        }
        if (type.cost != 1) {
            buf.writeByte(12)
            buf.writeInt(type.cost)
        }
        if (type.wearPos1 != -1) {
            buf.writeByte(13)
            buf.writeByte(type.wearPos1)
        }
        if (type.wearPos2 != -1) {
            buf.writeByte(14)
            buf.writeByte(type.wearPos2)
        }
        if (type.members) {
            buf.writeByte(16)
        }
        if (type.maleModel0 != 0) {
            buf.writeByte(23)
            buf.writeShort(type.maleModel0)
            buf.writeByte(type.maleModelOffset)
        }
        if (type.maleModel1 != 0) {
            buf.writeByte(24)
            buf.writeShort(type.maleModel1)
        }
        if (type.femaleModel0 != 0) {
            buf.writeByte(25)
            buf.writeShort(type.femaleModel0)
            buf.writeByte(type.femaleModelOffset)
        }
        if (type.femaleModel1 != 0) {
            buf.writeByte(26)
            buf.writeShort(type.femaleModel1)
        }
        if (type.wearPos3 != -1) {
            buf.writeByte(27)
            buf.writeByte(type.wearPos3)
        }
        type.groundOptions.forEachIndexed { i, str ->
            if (str != null && str != "Hidden" && str != "Take") {
                buf.writeByte(30 + i)
                buf.writeString(str)
            }
        }
        type.inventoryOptions.forEachIndexed { i, str ->
            if (str != null && str != "Hidden" && str != "Drop") {
                buf.writeByte(35 + i)
                buf.writeString(str)
            }
        }
        if (type.recolorSrc.isNotEmpty()) {
            buf.writeByte(40)
            buf.writeByte(type.recolorSrc.size)
            for (i in type.recolorSrc.indices) {
                buf.writeShort(type.recolorSrc[i])
                buf.writeShort(type.recolorDest[i])
            }
        }
        if (type.retextureSrc.isNotEmpty()) {
            buf.writeByte(41)
            buf.writeByte(type.retextureSrc.size)
            for (i in type.retextureSrc.indices) {
                buf.writeShort(type.retextureSrc[i])
                buf.writeShort(type.retextureDest[i])
            }
        }
        if (type.dropOptionIndex != -2) {
            buf.writeByte(42)
            buf.writeByte(type.dropOptionIndex)
        }
        if (type.exchangeable) {
            buf.writeByte(65)
        }
        if (type.weight != 0) {
            buf.writeByte(75)
            buf.writeShort(type.weight)
        }
        if (type.maleModel2 != 0) {
            buf.writeByte(78)
            buf.writeShort(type.maleModel2)
        }
        if (type.femaleModel2 != 0) {
            buf.writeByte(79)
            buf.writeShort(type.femaleModel2)
        }
        if (type.maleHeadModel0 != 0) {
            buf.writeByte(90)
            buf.writeShort(type.maleHeadModel0)
        }
        if (type.femaleHeadModel0 != 0) {
            buf.writeByte(91)
            buf.writeShort(type.femaleHeadModel0)
        }
        if (type.maleHeadModel1 != 0) {
            buf.writeByte(92)
            buf.writeShort(type.maleHeadModel1)
        }
        if (type.femaleHeadModel1 != 0) {
            buf.writeByte(93)
            buf.writeShort(type.femaleHeadModel1)
        }
        type.categories.forEach { category ->
            buf.writeByte(94)
            buf.writeShort(category)
        }
        if (type.zan2d != 0) {
            buf.writeByte(95)
            buf.writeShort(type.zan2d)
        }
        if (type.noteLink != 0) {
            buf.writeByte(97)
            buf.writeShort(type.noteLink)
        }
        if (type.noteModel != 0) {
            buf.writeByte(98)
            buf.writeShort(type.noteModel)
        }
        type.countItem.forEachIndexed { i, obj ->
            val countCo = type.countCo[i]
            buf.writeByte(100 + i)
            buf.writeShort(obj)
            buf.writeShort(countCo)
        }
        if (type.resizeX != 128) {
            buf.writeByte(110)
            buf.writeShort(type.resizeX)
        }
        if (type.resizeY != 128) {
            buf.writeByte(111)
            buf.writeShort(type.resizeY)
        }
        if (type.resizeZ != 128) {
            buf.writeByte(112)
            buf.writeShort(type.resizeZ)
        }
        if (type.ambient != 0) {
            buf.writeByte(113)
            buf.writeByte(type.ambient)
        }
        if (type.contrast != 0) {
            buf.writeByte(114)
            buf.writeByte(type.contrast)
        }
        if (type.team != 0) {
            buf.writeByte(115)
            buf.writeByte(type.team)
        }
        if (type.boughtLink != 0) {
            buf.writeByte(139)
            buf.writeShort(type.boughtLink)
        }
        if (type.boughtValue != 0) {
            buf.writeByte(140)
            buf.writeShort(type.boughtValue)
        }
        if (type.placeholderLink != 0) {
            buf.writeByte(148)
            buf.writeShort(type.placeholderLink)
        }
        if (type.placeholderModel != 0) {
            buf.writeByte(149)
            buf.writeShort(type.placeholderModel)
        }
        if (type.params != null) {
            buf.writeByte(249)
            buf.writeParams(type.params, params)
        }
        if (!isJs5) {
            type.internalName?.let {
                buf.writeByte(ConfigType.INTERNAL_NAME_OPCODE)
                buf.writeString(it)
            }
        }
        buf.writeByte(0)
    }
}
