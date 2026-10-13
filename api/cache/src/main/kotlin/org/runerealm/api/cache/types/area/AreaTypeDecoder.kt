package org.runerealm.api.cache.types.area

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.io.IOException
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.runerealm.api.cache.Js5Archives
import org.runerealm.api.cache.Js5Configs
import org.runerealm.api.cache.util.TextUtil
import org.runerealm.game.type.TypeResolver
import org.runerealm.game.type.area.AreaTypeBuilder
import org.runerealm.game.type.area.AreaTypeList
import org.runerealm.game.type.area.UnpackedAreaType

public object AreaTypeDecoder {
    public fun decodeAll(cache: Cache): AreaTypeList {
        val types = Int2ObjectOpenHashMap<UnpackedAreaType>()
        val files = cache.list(Js5Archives.CONFIG, Js5Configs.AREA)
        for (file in files) {
            val data = cache.read(Js5Archives.CONFIG, Js5Configs.AREA, file.id)
            val type = data.use { decode(it).build(file.id) }
            types[file.id] = type.apply { TypeResolver[this] = file.id }
        }
        return AreaTypeList(types)
    }

    public fun decode(data: ByteBuf): AreaTypeBuilder {
        val builder = AreaTypeBuilder(TextUtil.NULL)
        while (data.isReadable) {
            val code = data.readUnsignedByte().toInt()
            if (code == 0) {
                break
            }
            decode(builder, data, code)
        }
        return builder
    }

    public fun decode(builder: AreaTypeBuilder, data: ByteBuf, code: Int): Unit =
        with(builder) {
            when (code) {
                200 -> colour = data.readInt()
                else -> throw IOException("Error unrecognised .area config code: $code")
            }
        }
}
