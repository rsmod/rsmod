package org.rsmod.plugins.api

import org.rsmod.game.map.Coordinates
import org.rsmod.game.map.zone.ZoneKey
import org.rsmod.game.model.BuildArea
import org.rsmod.plugins.api.util.BuildAreaUtils

public fun Coordinates.toBuildArea(): BuildArea {
    val zoneViewRadius = BuildAreaUtils.ZONE_VIEW_RADIUS
    val baseZone = ZoneKey.from(this).translate(-zoneViewRadius, -zoneViewRadius)
    return BuildArea(baseZone.toCoords())
}
