package org.rsmod.game.map

public class LocZoneStorage {
    public val mapLocs: ZoneLocMap = ZoneLocMap()

    public val spawnedLocs: ZoneLocMap = ZoneLocMap()

    public fun totalLocCount(): Int = mapLocCount() + spawnedLocCount()

    public fun mapLocCount(): Int = mapLocs.locCount()

    public fun spawnedLocCount(): Int = spawnedLocs.locCount()

    public fun totalZoneCount(): Int = mapZoneCount() + spawnedLocs.zoneCount

    public fun mapZoneCount(): Int = mapLocs.zoneCount
}
