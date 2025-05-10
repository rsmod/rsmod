package org.rsmod.api.cache.map.npc

import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.util.EncoderContext
import org.rsmod.api.cache.util.readOrNull
import org.rsmod.map.square.MapSquareKey

public object MapNpcListEncoder {
    public fun encodeAll(
        cache: Cache,
        spawns: Map<MapSquareKey, MapNpcListDefinition>,
        ctx: EncoderContext,
    ) {
        // Map npc spawns are a server-only group.
        if (ctx.clientOnly) {
            return
        }
        val buffer = PooledByteBufAllocator.DEFAULT.buffer()
        val archive = Js5Archives.MAPS
        for ((key, definition) in spawns) {
            val group = "n${key.x}_${key.z}"
            val oldBuf = cache.readOrNull(archive, group, file = 0)
            val newBuf = buffer.clear().apply { encode(definition, this) }
            if (newBuf != oldBuf) {
                cache.write(archive, group, file = 0, newBuf)
            }
            oldBuf?.release()
        }
        buffer.release()
    }

    public fun encode(definition: MapNpcListDefinition, data: ByteBuf): Unit =
        with(definition) {
            check(packedSpawns.size <= 65535) {
                "Map npc spawn size exceeds limit: ${packedSpawns.size} / 65535"
            }
            data.writeShort(packedSpawns.size)
            for (packed in packedSpawns.intIterator()) {
                data.writeInt(packed)
            }
        }
}
