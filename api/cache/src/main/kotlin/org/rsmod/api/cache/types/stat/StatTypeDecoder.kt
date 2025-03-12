package org.rsmod.api.cache.types.stat

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.openrs2.buffer.readString
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.TextUtil
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.stat.StatTypeBuilder
import org.rsmod.game.type.stat.StatTypeList
import org.rsmod.game.type.stat.UnpackedStatType

public object StatTypeDecoder {
    public fun decodeAll(cache: Cache, symbols: Map<String, Int>): StatTypeList {
        // Stat types exist in the game but are not transmitted as a config. However, in our use
        // case, other transmitted types (such as params and enums) depend on stat types. To ensure
        // compatibility, we provide a "default" stat type list that can be used before packing our
        // custom stat configs into the cache.
        if (!cache.exists(Js5Archives.CONFIG, Js5Configs.STAT)) {
            return createDefaultStatTypeList(symbols)
        }
        val types = Int2ObjectOpenHashMap<UnpackedStatType>()
        val files = cache.list(Js5Archives.CONFIG, Js5Configs.STAT)
        for (file in files) {
            val data = cache.read(Js5Archives.CONFIG, Js5Configs.STAT, file.id)
            val type = data.use { decode(it).build(file.id) }
            types[file.id] = type.apply { TypeResolver[this] = file.id }
        }
        return StatTypeList(types)
    }

    public fun decode(data: ByteBuf): StatTypeBuilder {
        val builder = StatTypeBuilder(TextUtil.NULL)
        while (data.isReadable) {
            val code = data.readUnsignedByte().toInt()
            if (code == 0) {
                break
            }
            decode(builder, data, code)
        }
        return builder
    }

    public fun decode(builder: StatTypeBuilder, data: ByteBuf, code: Int): Unit =
        with(builder) {
            when (code) {
                1 -> displayName = data.readString()
                2 -> unreleased = true
                3 -> maxLevel = data.readUnsignedByte().toInt()
            }
        }

    public fun assignInternal(list: StatTypeList, names: Map<String, Int>) {
        val reversedLookup = names.entries.associate { it.value to it.key }
        val types = list.values
        for (type in types) {
            val id = TypeResolver[type]
            val name = reversedLookup[id] ?: continue
            TypeResolver[type] = name
        }
    }

    private fun createDefaultStatTypeList(symbols: Map<String, Int>): StatTypeList {
        val stats = Int2ObjectOpenHashMap<UnpackedStatType>()
        for ((name, id) in symbols) {
            val builder = StatTypeBuilder(name)
            stats[id] = builder.build(id)
        }
        return StatTypeList(stats)
    }
}
