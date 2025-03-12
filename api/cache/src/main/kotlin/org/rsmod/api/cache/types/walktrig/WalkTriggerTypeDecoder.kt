package org.rsmod.api.cache.types.walktrig

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.TextUtil
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.walktrig.WalkTriggerPriority
import org.rsmod.game.type.walktrig.WalkTriggerType
import org.rsmod.game.type.walktrig.WalkTriggerTypeBuilder
import org.rsmod.game.type.walktrig.WalkTriggerTypeList

public object WalkTriggerTypeDecoder {
    public fun decodeAll(cache: Cache, symbols: Map<String, Int>): WalkTriggerTypeList {
        if (!cache.exists(Js5Archives.CONFIG, Js5Configs.WALKTRIGGER)) {
            return createDefaultWalkTriggerTypeList(symbols)
        }
        val types = Int2ObjectOpenHashMap<WalkTriggerType>()
        val files = cache.list(Js5Archives.CONFIG, Js5Configs.WALKTRIGGER)
        for (file in files) {
            val data = cache.read(Js5Archives.CONFIG, Js5Configs.WALKTRIGGER, file.id)
            val type = data.use { decode(it).build(file.id) }
            types[file.id] = type.apply { TypeResolver[this] = file.id }
        }
        return WalkTriggerTypeList(types)
    }

    public fun decode(data: ByteBuf): WalkTriggerTypeBuilder {
        val builder = WalkTriggerTypeBuilder(TextUtil.NULL)
        while (data.isReadable) {
            val code = data.readUnsignedByte().toInt()
            if (code == 0) {
                break
            }
            decode(builder, data, code)
        }
        return builder
    }

    public fun decode(builder: WalkTriggerTypeBuilder, data: ByteBuf, code: Int): Unit =
        with(builder) {
            when (code) {
                1 -> {
                    val id = data.readUnsignedByte().toInt()
                    val priority = WalkTriggerPriority[id]
                    checkNotNull(priority) { "Unexpected priority id: $id" }
                    this.priority = priority
                }
            }
        }

    public fun assignInternal(list: WalkTriggerTypeList, names: Map<String, Int>) {
        val reversedLookup = names.entries.associate { it.value to it.key }
        val types = list.values
        for (type in types) {
            val id = TypeResolver[type]
            val name = reversedLookup[id] ?: continue
            TypeResolver[type] = name
        }
    }

    private fun createDefaultWalkTriggerTypeList(symbols: Map<String, Int>): WalkTriggerTypeList {
        val walkTriggers = Int2ObjectOpenHashMap<WalkTriggerType>()
        for ((name, id) in symbols) {
            val builder = WalkTriggerTypeBuilder(name)
            walkTriggers[id] = builder.build(id)
        }
        return WalkTriggerTypeList(walkTriggers)
    }
}
