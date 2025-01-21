package org.rsmod.api.cache.types.varp

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.io.IOException
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.TextUtil
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.varp.UnpackedVarpType
import org.rsmod.game.type.varp.VarpTypeBuilder
import org.rsmod.game.type.varp.VarpTypeList

public object VarpTypeDecoder {
    public fun decodeAll(cache: Cache): VarpTypeList {
        val types = Int2ObjectOpenHashMap<UnpackedVarpType>()
        val files = cache.list(Js5Archives.CONFIG, Js5Configs.VARPLAYER)
        for (file in files) {
            val data = cache.read(Js5Archives.CONFIG, Js5Configs.VARPLAYER, file.id)
            val type = data.use { decode(it).build(file.id) }
            types[file.id] = type.apply { TypeResolver[this] = file.id }
        }
        return VarpTypeList(types)
    }

    public fun decode(data: ByteBuf): VarpTypeBuilder {
        val builder = VarpTypeBuilder(TextUtil.NULL)
        while (data.isReadable) {
            val code = data.readUnsignedByte().toInt()
            if (code == 0) {
                break
            }
            decode(builder, data, code)
        }
        return builder
    }

    public fun decode(builder: VarpTypeBuilder, data: ByteBuf, code: Int): Unit =
        with(builder) {
            when (code) {
                5 -> clientCode = data.readUnsignedShort()
                200 -> transmit = false
                201 -> protect = true
                else -> throw IOException("Error unrecognised .varp config code: $code")
            }
        }

    public fun assignInternal(list: VarpTypeList, names: Map<String, Int>) {
        val reversedLookup = names.entries.associate { it.value to it.key }
        val types = list.values
        for (type in types) {
            val id = TypeResolver[type]
            val name = reversedLookup[id] ?: continue
            TypeResolver[type] = name
        }
    }
}
