package org.rsmod.plugins.api.map.builder

import org.rsmod.game.map.util.collect.ImmutableZoneMap
import org.rsmod.game.map.util.collect.MutableZoneMap
import org.rsmod.game.map.zone.ZoneKey
import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.plugins.api.map.GameMap

@DslMarker
private annotation class GameMapBuilderDslMarker

@GameMapBuilderDslMarker
public class GameMapBuilder(public val flags: CollisionFlagMap = CollisionFlagMap()) {

    private val zones: MutableMap<ZoneKey, ZoneBuilder> = mutableMapOf()

    public fun computeIfAbsent(key: ZoneKey, value: () -> ZoneBuilder): ZoneBuilder {
        return zones.computeIfAbsent(key) { value() }
    }

    public fun build(): GameMap {
        // TODO: make use of [ZoneBuilder.linkBelow] object list.
        return GameMap(
            staticZones = buildStaticZones(),
            dynamicZones = MutableZoneMap.empty(),
            flags = flags
        )
    }

    private fun buildStaticZones(): ImmutableZoneMap {
        val staticZones = MutableZoneMap.empty(zones.size)
        zones.forEach { (key, builder) ->
            if (staticZones.containsKey(key.packed)) {
                error("Zone key already associated with value. (key=$key)")
            }
            staticZones[key.packed] = builder.build()
        }
        return staticZones.immutable()
    }
}
