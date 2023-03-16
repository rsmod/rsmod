package org.rsmod.plugins.api.map.builder

import org.rsmod.game.map.Coordinates
import org.rsmod.game.map.entity.obj.ObjectEntity
import org.rsmod.game.map.entity.obj.ObjectKey
import org.rsmod.game.map.util.collect.MutableObjectMap
import org.rsmod.game.map.zone.Zone

@DslMarker
private annotation class ZoneBuilderDslMarker

@ZoneBuilderDslMarker
public class ZoneBuilder {

    private val objects: MutableObjectMap = MutableObjectMap.empty()

    public val linkBelow: MutableObjectMap = MutableObjectMap.empty()

    public fun add(coords: Coordinates, slot: Int, entity: ObjectEntity) {
        val key = ObjectKey(coords.x.toZoneLocal(), coords.z.toZoneLocal(), slot)
        objects[key.packed] = entity.packed
    }

    public fun addLinkBelow(coords: Coordinates, slot: Int, entity: ObjectEntity) {
        val key = ObjectKey(coords.x.toZoneLocal(), coords.z.toZoneLocal(), slot)
        linkBelow[key.packed] = entity.packed
    }

    public fun build(): Zone {
        return Zone(objects.immutable())
    }

    private fun Int.toZoneLocal(): Int = this and (Zone.SIZE - 1)
}
