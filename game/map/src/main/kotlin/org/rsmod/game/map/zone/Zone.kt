package org.rsmod.game.map.zone

import it.unimi.dsi.fastutil.bytes.Byte2IntMap
import org.rsmod.game.map.entity.obj.ObjectEntity
import org.rsmod.game.map.entity.obj.ObjectKey
import org.rsmod.game.map.util.collect.ImmutableObjectMap
import org.rsmod.game.map.util.collect.MutableObjectMap

public data class Zone(public val staticObjects: ImmutableObjectMap) {

    public var dynamicObjects: MutableObjectMap? = null

    public operator fun set(key: ObjectKey, obj: ObjectEntity) {
        if (dynamicObjects == null) {
            dynamicObjects = MutableObjectMap.empty(DYNAMIC_OBJECT_MAP_INITIAL_CAP)
        }
        dynamicObjects?.set(key.packed, obj.packed)
    }

    public operator fun get(key: Byte): Int? {
        return dynamicObjects?.get(key) ?: staticObjects[key]
    }

    public fun getValue(key: Byte): Int {
        return this[key] ?: throw NoSuchElementException("Key $key is missing in the map.")
    }

    public fun entrySet(): Set<Byte2IntMap.Entry> {
        return dynamicObjects?.let { it.entrySet() + staticObjects.entrySet() }
            ?: staticObjects.entrySet()
    }

    public companion object {

        public const val SIZE: Int = 8

        private const val DYNAMIC_OBJECT_MAP_INITIAL_CAP: Int = 16
    }
}
