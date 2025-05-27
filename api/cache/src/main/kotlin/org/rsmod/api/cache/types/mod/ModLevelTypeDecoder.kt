package org.rsmod.api.cache.types.mod

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.io.IOException
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.TextUtil
import org.rsmod.api.cache.util.listOrEmpty
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.mod.ModLevelTypeBuilder
import org.rsmod.game.type.mod.ModLevelTypeList
import org.rsmod.game.type.mod.UnpackedModLevelType

public object ModLevelTypeDecoder {
    public fun decodeAll(cache: Cache): ModLevelTypeList {
        val types = Int2ObjectOpenHashMap<UnpackedModLevelType>()
        val files = cache.listOrEmpty(Js5Archives.CONFIG, Js5Configs.MODLEVEL)
        for (file in files) {
            val data = cache.read(Js5Archives.CONFIG, Js5Configs.MODLEVEL, file.id)
            val type = data.use { decode(it).build(file.id) }
            types[file.id] = type.apply { TypeResolver[this] = file.id }
        }
        return ModLevelTypeList(types)
    }

    public fun decode(data: ByteBuf): ModLevelTypeBuilder {
        val builder = ModLevelTypeBuilder(TextUtil.NULL)
        while (data.isReadable) {
            val code = data.readUnsignedByte().toInt()
            if (code == 0) {
                break
            }
            decode(builder, data, code)
        }
        return builder
    }

    public fun decode(builder: ModLevelTypeBuilder, data: ByteBuf, code: Int): Unit =
        with(builder) {
            when (code) {
                1 -> clientCode = data.readUnsignedByte().toInt()
                2 -> accessFlags = data.readLong()
                else -> throw IOException("Error unrecognised .modlevel config code: $code")
            }
        }
}
