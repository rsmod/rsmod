package org.rsmod.api.cache.map.loc

import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import org.openrs2.cache.Cache
import org.openrs2.crypto.SymmetricKey
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.util.EncoderContext
import org.rsmod.api.cache.util.writeUnsignedSmartInt
import org.rsmod.game.map.xtea.XteaMap
import org.rsmod.map.square.MapSquareKey

public object MapLocListEncoder {
    public fun encodeAll(
        cache: Cache,
        spawns: Map<MapSquareKey, MapLocListDefinition>,
        xteaMap: XteaMap,
        ctx: EncoderContext,
    ) {
        val buffer = PooledByteBufAllocator.DEFAULT.buffer()
        val archive = Js5Archives.MAPS
        for ((key, definition) in spawns) {
            val group = "l${key.x}_${key.z}"
            val newBuf = buffer.clear().apply { encode(definition, this) }
            val xtea = xteaMap[key]?.let(SymmetricKey::fromIntArray) ?: SymmetricKey.ZERO
            cache.write(archive, group, file = 0, newBuf, key = xtea)
        }
        buffer.release()
    }

    public fun encode(definition: MapLocListDefinition, data: ByteBuf) {
        var lastType = -1
        var lastCoord = 0
        val sorted =
            definition.spawns
                .map(::MapLocDefinition)
                .sortedWith(compareBy(MapLocDefinition::id, MapLocDefinition::packedCoord))
        for (loc in sorted) {
            val newLocType = loc.id != lastType
            if (newLocType) {
                val addTerminator = lastType != -1
                if (addTerminator) {
                    data.writeUnsignedSmartInt(0)
                }
                val typeDiff = loc.id - lastType
                lastType = loc.id
                lastCoord = 0

                data.writeUnsignedSmartInt(typeDiff)
            }
            val coord = loc.packedCoord()
            val coordDiff = coord - lastCoord + 1
            lastCoord = coord

            data.writeUnsignedSmartInt(coordDiff)
            data.writeByte(loc.packedAttributes())
        }
        val addTerminator = lastType != -1
        if (addTerminator) {
            data.writeUnsignedSmartInt(0)
        }
        data.writeUnsignedSmartInt(0)
    }
}
