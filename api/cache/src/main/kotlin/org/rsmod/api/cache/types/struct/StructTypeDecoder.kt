package org.rsmod.api.cache.types.struct

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.io.IOException
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.TextUtil
import org.rsmod.api.cache.util.readRawParams
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.struct.StructTypeBuilder
import org.rsmod.game.type.struct.StructTypeList
import org.rsmod.game.type.struct.UnpackedStructType
import org.rsmod.game.type.util.ParamMap

public object StructTypeDecoder {
    public fun decodeAll(cache: Cache): StructTypeList {
        val types = Int2ObjectOpenHashMap<UnpackedStructType>()
        val files = cache.list(Js5Archives.CONFIG, Js5Configs.STRUCT)
        for (file in files) {
            val data = cache.read(Js5Archives.CONFIG, Js5Configs.STRUCT, file.id)
            val type = data.use { decode(it).build(file.id) }
            types[file.id] = type.apply { TypeResolver[this] = file.id }
        }
        return StructTypeList(types)
    }

    public fun decode(data: ByteBuf): StructTypeBuilder {
        val builder = StructTypeBuilder(TextUtil.NULL)
        while (data.isReadable) {
            val code = data.readUnsignedByte().toInt()
            if (code == 0) {
                break
            }
            decode(builder, data, code)
        }
        return builder
    }

    public fun decode(builder: StructTypeBuilder, data: ByteBuf, code: Int): Unit =
        with(builder) {
            when (code) {
                249 -> paramMap = ParamMap(data.readRawParams())
                else -> throw IOException("Error unrecognised .struct config code: $code")
            }
        }
}
