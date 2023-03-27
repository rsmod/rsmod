package org.rsmod.plugins.api.map

import org.rsmod.game.map.Coordinates
import org.rsmod.game.map.entity.obj.ObjectEntity
import org.rsmod.game.map.entity.obj.ObjectEntry
import org.rsmod.game.map.entity.obj.ObjectKey
import org.rsmod.game.map.util.collect.ImmutableZoneMap
import org.rsmod.game.map.util.collect.MutableZoneMap
import org.rsmod.game.map.zone.Zone
import org.rsmod.game.map.zone.ZoneKey
import org.rsmod.game.pathfinder.collision.CollisionFlagMap

public class GameMap(
    private val staticZones: ImmutableZoneMap,
    private val dynamicZones: MutableZoneMap,
    public val flags: CollisionFlagMap
) {

    public operator fun get(key: ZoneKey): Zone? = if (key.isDynamicZone()) {
        dynamicZones[key.packed]
    } else {
        staticZones[key.packed]
    }

    public fun objectEntries(coords: Coordinates): List<ObjectEntry> {
        val key = ZoneKey.from(coords)
        val zone = this[key] ?: return emptyList()
        val zoneCoords = key.toCoords()
        return zone.entrySet()
            .mapNotNull { entry ->
                val objKey = ObjectKey(entry.byteKey)
                val objCoords = zoneCoords.translate(objKey.x, objKey.z)
                if (objCoords != coords) return@mapNotNull null
                ObjectEntry(objKey.slot, objCoords, ObjectEntity(entry.intValue))
            }
    }

    public fun objectEntries(key: ZoneKey): List<ObjectEntry> {
        val zone = this[key] ?: return emptyList()
        val zoneCoords = key.toCoords()
        return zone.entrySet()
            .map { entry ->
                val objKey = ObjectKey(entry.byteKey)
                val objCoords = zoneCoords.translate(objKey.x, objKey.z)
                ObjectEntry(objKey.slot, objCoords, ObjectEntity(entry.intValue))
            }
    }

    public companion object {

        private const val START_DYNAMIC_ZONE_X: Int = 6400 shr 3

        private fun ZoneKey.isDynamicZone(): Boolean {
            return x >= START_DYNAMIC_ZONE_X
        }
    }
}
