package org.rsmod.api.cache.map.loc

public data class MapLocDefinition(private val locs: List<Long>) : List<Long> by locs {
    public fun toTypedList(): List<MapLoc> = locs.map(::MapLoc)
}
