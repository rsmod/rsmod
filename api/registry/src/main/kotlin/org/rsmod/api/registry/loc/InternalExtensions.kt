package org.rsmod.api.registry.loc

import org.rsmod.game.loc.LocEntity
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.loc.LocZoneKey
import org.rsmod.game.map.ZoneLocMap
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneGrid
import org.rsmod.map.zone.ZoneKey
import org.rsmod.routefinder.loc.LocLayerConstants

internal fun ZoneLocMap.findShape(coords: CoordGrid, shape: Int): LocInfo? {
    val layer = LocLayerConstants.of(shape)
    val layerLoc = findLayer(coords, layer) ?: return null
    return layerLoc.takeIf { it.shapeId == shape }
}

internal fun ZoneLocMap.findLayer(coords: CoordGrid, layer: Int): LocInfo? {
    val zoneKey = ZoneKey.from(coords)
    val zoneGrid = ZoneGrid.from(coords)

    val locZoneKey = LocZoneKey(zoneGrid.x, zoneGrid.z, layer)
    val packedEntity = this[zoneKey, locZoneKey] ?: return null

    return LocInfo(layer, coords, LocEntity(packedEntity))
}

internal fun ZoneLocMap.findType(coords: CoordGrid, id: Int): LocInfo? {
    val zoneKey = ZoneKey.from(coords)
    val entries = this[zoneKey]?.byte2IntEntrySet() ?: return null
    val zoneCoords = zoneKey.toCoords()
    for (entry in entries) {
        val locEntity = LocEntity(entry.intValue)
        if (locEntity.id != id) {
            continue
        }
        val locKey = LocZoneKey(entry.byteKey)
        val locCoords = zoneCoords.translate(locKey.x, locKey.z)
        if (locCoords != coords) {
            continue
        }
        return LocInfo(locKey.layer, locCoords, locEntity)
    }
    return null
}

private fun CoordGrid.toLocZoneGridKey(layer: Int): LocZoneKey {
    val zoneGrid = ZoneGrid.from(this)
    return LocZoneKey(zoneGrid.x, zoneGrid.z, layer)
}

internal fun LocInfo.toLocZoneGridKey(): LocZoneKey = coords.toLocZoneGridKey(layer)
