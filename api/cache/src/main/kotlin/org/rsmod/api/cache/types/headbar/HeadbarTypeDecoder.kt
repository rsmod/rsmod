package org.rsmod.api.cache.types.headbar

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.io.IOException
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.TextUtil
import org.rsmod.api.cache.util.readNullableLargeSmart
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.headbar.HeadbarTypeBuilder
import org.rsmod.game.type.headbar.HeadbarTypeList
import org.rsmod.game.type.headbar.UnpackedHeadbarType

public object HeadbarTypeDecoder {
    public fun decodeAll(cache: Cache): HeadbarTypeList {
        val types = Int2ObjectOpenHashMap<UnpackedHeadbarType>()
        val files = cache.list(Js5Archives.CONFIG, Js5Configs.HEADBAR)
        for (file in files) {
            val data = cache.read(Js5Archives.CONFIG, Js5Configs.HEADBAR, file.id)
            val type = data.use { decode(it).build(file.id) }
            types[file.id] = type.apply { TypeResolver[this] = file.id }
        }
        return HeadbarTypeList(types)
    }

    public fun decode(data: ByteBuf): HeadbarTypeBuilder {
        val builder = HeadbarTypeBuilder(TextUtil.NULL)
        while (data.isReadable) {
            val code = data.readUnsignedByte().toInt()
            if (code == 0) {
                break
            }
            decode(builder, data, code)
        }
        return builder
    }

    public fun decode(builder: HeadbarTypeBuilder, data: ByteBuf, code: Int): Unit =
        with(builder) {
            when (code) {
                1 -> unknown1 = data.readUnsignedShort()
                2 -> showPriority = data.readUnsignedByte().toInt()
                3 -> hidePriority = data.readUnsignedByte().toInt()
                4 -> fadeout = 0
                5 -> stickTime = data.readUnsignedShort()
                6 -> unknown6 = data.readUnsignedByte().toInt()
                7 -> full = data.readNullableLargeSmart()
                8 -> empty = data.readNullableLargeSmart()
                11 -> fadeout = data.readUnsignedShort()
                14 -> segments = data.readUnsignedByte().toInt()
                15 -> padding = data.readUnsignedByte().toInt()
                else -> throw IOException("Error unrecognised .headbar config code: $code")
            }
        }
}
