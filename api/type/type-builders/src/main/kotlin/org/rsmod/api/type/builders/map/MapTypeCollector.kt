package org.rsmod.api.type.builders.map

import jakarta.inject.Inject
import org.rsmod.api.cache.map.area.MapAreaDefinition
import org.rsmod.api.type.builders.map.area.MapAreaBuilder
import org.rsmod.api.type.builders.map.area.MapAreaCollector
import org.rsmod.map.square.MapSquareKey

public class MapTypeCollector @Inject constructor(private val areas: MapAreaCollector) {
    public fun areas(builders: Iterable<MapAreaBuilder>): Map<MapSquareKey, MapAreaDefinition> {
        return areas.loadAndCollect(builders)
    }
}
