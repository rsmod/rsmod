package org.rsmod.api.cache.types.dbtable

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.io.IOException
import org.openrs2.buffer.readUnsignedShortSmart
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.TextUtil
import org.rsmod.api.cache.util.readColumnValues
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.dbtable.DbTableTypeBuilder
import org.rsmod.game.type.dbtable.DbTableTypeList
import org.rsmod.game.type.dbtable.UnpackedDbTableType

public object DbTableTypeDecoder {
    public fun decodeAll(cache: Cache): DbTableTypeList {
        val types = Int2ObjectOpenHashMap<UnpackedDbTableType>()
        val files = cache.list(Js5Archives.CONFIG, Js5Configs.DBTABLETYPE)
        for (file in files) {
            val data = cache.read(Js5Archives.CONFIG, Js5Configs.DBTABLETYPE, file.id)
            val type = data.use { decode(it).build(file.id) }
            types[file.id] = type.apply { TypeResolver[this] = file.id }
        }
        return DbTableTypeList(types)
    }

    public fun decode(data: ByteBuf): DbTableTypeBuilder {
        val builder = DbTableTypeBuilder(TextUtil.NULL)
        while (data.isReadable) {
            val code = data.readUnsignedByte().toInt()
            if (code == 0) {
                break
            }
            decode(builder, data, code)
        }
        return builder
    }

    public fun decode(builder: DbTableTypeBuilder, data: ByteBuf, code: Int): Unit =
        with(builder) {
            when (code) {
                1 -> {
                    val columnTypes = Int2ObjectOpenHashMap<List<Int>>()
                    val columnDefaults = Int2ObjectOpenHashMap<List<Any>>()
                    val columnAttributes = Int2IntOpenHashMap()

                    val columnCount = data.readUnsignedByte().toInt()
                    var attributes = data.readUnsignedByte().toInt()
                    while (attributes != 0xFF) {
                        val column = attributes and 0x7F

                        val typeCount = data.readUnsignedByte().toInt()
                        val types = (0 until typeCount).map { data.readUnsignedShortSmart() }
                        columnTypes[column] = types

                        val required = (attributes and UnpackedDbTableType.REQUIRED) != 0
                        if (required) {
                            columnDefaults[column] = data.readColumnValues(types)
                        }

                        val serverAttributes = attributes or DbTableTypeBuilder.DEFAULT_ATTRIBUTES
                        columnAttributes[column] = serverAttributes

                        attributes = data.readUnsignedByte().toInt()
                    }

                    this.types = columnTypes
                    this.defaults = columnDefaults
                    this.attributes = columnAttributes
                    this.columnCount = columnCount
                }
                200 -> {
                    val columnAttributes = Int2IntOpenHashMap()
                    var attributes = data.readUnsignedShort()
                    while (attributes != 0xFFFF) {
                        val column = attributes and 0x7F
                        columnAttributes[column] = attributes
                        attributes = data.readUnsignedShort()
                    }
                    this.attributes = columnAttributes
                }
                else -> throw IOException("Error unrecognised .dbtable config code: $code")
            }
        }
}
