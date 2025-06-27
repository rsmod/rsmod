package org.rsmod.api.cache.types.proj

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.io.IOException
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.TextUtil
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.proj.ProjAnimTypeBuilder
import org.rsmod.game.type.proj.ProjAnimTypeList
import org.rsmod.game.type.proj.UnpackedProjAnimType

public object ProjAnimTypeDecoder {
    public fun decodeAll(cache: Cache): ProjAnimTypeList {
        if (!cache.exists(Js5Archives.CONFIG, Js5Configs.PROJANIM)) {
            return ProjAnimTypeList(mutableMapOf())
        }
        val types = Int2ObjectOpenHashMap<UnpackedProjAnimType>()
        val files = cache.list(Js5Archives.CONFIG, Js5Configs.PROJANIM)
        for (file in files) {
            val data = cache.read(Js5Archives.CONFIG, Js5Configs.PROJANIM, file.id)
            val type = data.use { decode(it).build(file.id) }
            types[file.id] = type.apply { TypeResolver[this] = file.id }
        }
        return ProjAnimTypeList(types)
    }

    public fun decode(data: ByteBuf): ProjAnimTypeBuilder {
        val builder = ProjAnimTypeBuilder(TextUtil.NULL)
        while (data.isReadable) {
            val code = data.readUnsignedByte().toInt()
            if (code == 0) {
                break
            }
            decode(builder, data, code)
        }
        return builder
    }

    public fun decode(builder: ProjAnimTypeBuilder, data: ByteBuf, code: Int): Unit =
        with(builder) {
            when (code) {
                1 -> startHeight = data.readUnsignedByte().toInt()
                2 -> endHeight = data.readUnsignedByte().toInt()
                3 -> delay = data.readUnsignedByte().toInt()
                4 -> angle = data.readUnsignedByte().toInt()
                5 -> lengthAdjustment = data.readByte().toInt()
                6 -> progress = data.readUnsignedByte().toInt()
                7 -> stepMultiplier = data.readUnsignedByte().toInt()
                8 -> startHeight = data.readUnsignedShort()
                9 -> endHeight = data.readUnsignedShort()
                else -> throw IOException("Error unrecognised .projanim config code: $code")
            }
        }
}
