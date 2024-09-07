package org.rsmod.api.utils.map.zone

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unimi.dsi.fastutil.ints.IntSet
import jakarta.inject.Inject
import java.util.EnumMap
import net.rsprot.protocol.api.util.ZonePartialEnclosedCacheBuffer
import net.rsprot.protocol.common.client.OldSchoolClientType
import org.rsmod.api.registry.zone.ZoneUpdateMap
import org.rsmod.api.registry.zone.ZoneUpdateTransformer
import org.rsmod.game.entity.PlayerList
import org.rsmod.map.zone.ZoneKey

public class SharedZoneEnclosedBuffers
@Inject
constructor(
    private val players: PlayerList,
    private val zoneUpdates: ZoneUpdateMap,
    private val enclosedCache: ZonePartialEnclosedCacheBuffer,
) {
    public val buffers: Int2ObjectMap<EnumMap<OldSchoolClientType, ByteBuf>> =
        Int2ObjectOpenHashMap()

    public val activeZones: IntSet = IntOpenHashSet()

    public fun computeSharedBuffers() {
        for (player in players) {
            activeZones.addAll(player.visibleZoneKeys)
        }
        for (zone in activeZones.intIterator()) {
            val updates = zoneUpdates[zone] ?: continue
            val protList = ZoneUpdateTransformer.collectEnclosedProtList(updates)
            // Some zone prots are always sent alongside `UpdateZonePartialFollows`, such as any
            // privately-owned obj `ObjAdd` updates.
            val filtered = protList.filterNot { it is ZoneUpdateTransformer.PartialFollowsZoneProt }
            if (filtered.isEmpty()) {
                continue
            }
            buffers[zone] = enclosedCache.computeZone(filtered)
        }
    }

    public fun clear() {
        activeZones.clear()
        buffers.clear()
        enclosedCache.releaseBuffers()
    }

    public operator fun get(zone: ZoneKey): EnumMap<OldSchoolClientType, ByteBuf>? =
        buffers.getOrDefault(zone.packed, null)
}
