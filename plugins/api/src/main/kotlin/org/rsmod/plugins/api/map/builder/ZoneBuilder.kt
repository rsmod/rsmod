package org.rsmod.plugins.api.map.builder

import org.rsmod.game.map.entity.obj.ObjectEntity
import org.rsmod.game.map.entity.obj.ObjectKey
import org.rsmod.game.map.util.I8Coordinates
import org.rsmod.game.map.util.collect.MutableObjectMap
import org.rsmod.game.map.zone.Zone

@DslMarker
private annotation class ZoneBuilderDslMarker

@ZoneBuilderDslMarker
public class ZoneBuilder {

    private val objects: MutableObjectMap = MutableObjectMap.empty()

    public val linkBelow: MutableObjectMap = MutableObjectMap.empty()

    public fun add(coords: I8Coordinates, slot: Int, entity: ObjectEntity) {
        val key = ObjectKey(coords.x, coords.z, slot)
        objects[key.packed] = entity.packed
    }

    public fun addLinkBelow(coords: I8Coordinates, slot: Int, entity: ObjectEntity) {
        val key = ObjectKey(coords.x, coords.z, slot)
        linkBelow[key.packed] = entity.packed
    }

    public fun build(): Zone {
        return Zone(objects.immutable())
    }
}
