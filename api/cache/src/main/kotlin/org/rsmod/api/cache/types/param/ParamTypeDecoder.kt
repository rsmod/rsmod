package org.rsmod.api.cache.types.param

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.io.IOException
import org.openrs2.buffer.readString
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.TextUtil
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.param.ParamTypeBuilder
import org.rsmod.game.type.param.ParamTypeList
import org.rsmod.game.type.param.UnpackedParamType

public object ParamTypeDecoder {
    public fun decodeAll(cache: Cache): ParamTypeList {
        val types = Int2ObjectOpenHashMap<UnpackedParamType<Any>>()
        val files = cache.list(Js5Archives.CONFIG, Js5Configs.PARAM)
        for (file in files) {
            val data = cache.read(Js5Archives.CONFIG, Js5Configs.PARAM, file.id)
            val type = data.use { decode(it).build(file.id) }
            types[file.id] = type.apply { TypeResolver[this] = file.id }
        }
        return ParamTypeList(types)
    }

    public fun decode(data: ByteBuf): ParamTypeBuilder<Any> {
        val builder = ParamTypeBuilder(Any::class)
        builder.internal = TextUtil.NULL
        while (data.isReadable) {
            val code = data.readUnsignedByte().toInt()
            if (code == 0) {
                break
            }
            decode(builder, data, code)
        }
        return builder
    }

    public fun decode(builder: ParamTypeBuilder<Any>, data: ByteBuf, code: Int): Unit =
        with(builder) {
            when (code) {
                1 -> typeCharId = data.readUnsignedByte().toInt().toChar()
                2 -> defaultInt = data.readInt()
                4 -> autoDisable = false
                5 -> defaultStr = data.readString()
                else -> throw IOException("Error unrecognised .param config code: $code")
            }
        }

    public fun assignInternal(list: ParamTypeList, names: Map<String, Int>) {
        val reversedLookup = names.entries.associate { it.value to it.key }
        val types = list.values
        for (type in types) {
            val id = TypeResolver[type]
            val name = reversedLookup[id] ?: continue
            TypeResolver[type] = name
        }
    }
}
