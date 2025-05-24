package org.rsmod.api.cache.types.dbrow

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.io.IOException
import org.openrs2.buffer.readUnsignedShortSmart
import org.openrs2.buffer.readVarInt
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.TextUtil
import org.rsmod.api.cache.util.readColumnValues
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.dbrow.DbRowTypeBuilder
import org.rsmod.game.type.dbrow.DbRowTypeList
import org.rsmod.game.type.dbrow.UnpackedDbRowType

public object DbRowTypeDecoder {
    public fun decodeAll(cache: Cache): DbRowTypeList {
        val types = Int2ObjectOpenHashMap<UnpackedDbRowType>()
        val files = cache.list(Js5Archives.CONFIG, Js5Configs.DBROW)
        for (file in files) {
            val data = cache.read(Js5Archives.CONFIG, Js5Configs.DBROW, file.id)
            val type = data.use { decode(it).build(file.id) }
            types[file.id] = type.apply { TypeResolver[this] = file.id }
        }
        return DbRowTypeList(types)
    }

    public fun decode(data: ByteBuf): DbRowTypeBuilder {
        val builder = DbRowTypeBuilder(TextUtil.NULL)
        while (data.isReadable) {
            val code = data.readUnsignedByte().toInt()
            if (code == 0) {
                break
            }
            decode(builder, data, code)
        }
        return builder
    }

    public fun decode(builder: DbRowTypeBuilder, data: ByteBuf, code: Int): Unit =
        with(builder) {
            when (code) {
                3 -> {
                    val columnData = Int2ObjectOpenHashMap<List<Any>>()
                    val columnTypes = Int2ObjectOpenHashMap<List<Int>>()

                    val columnCount = data.readUnsignedByte().toInt()
                    var column = data.readUnsignedByte().toInt()
                    while (column != 0xFF) {
                        val typeCount = data.readUnsignedByte().toInt()
                        val types = (0 until typeCount).map { data.readUnsignedShortSmart() }
                        columnTypes[column] = types

                        val values = data.readColumnValues(types)
                        columnData[column] = values

                        column = data.readUnsignedByte().toInt()
                    }

                    this.data = columnData
                    this.types = columnTypes
                    this.columnCount = columnCount
                }
                4 -> table = data.readVarInt()
                else -> throw IOException("Error unrecognised .dbrow config code: $code")
            }
        }
}
