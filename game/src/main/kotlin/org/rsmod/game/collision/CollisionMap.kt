package org.rsmod.game.collision

import com.google.inject.Inject
import org.rsmod.game.model.map.Coordinates

inline class CollisionTile(val flags: Int)

class CollisionMap(private val tiles: MutableMap<Coordinates, CollisionTile>) {

    @Inject
    constructor() : this(mutableMapOf())

    fun add(coordinates: Coordinates, mask: Int) {
        val old = tiles[coordinates]?.flags ?: 0
        this[coordinates] = old or mask
    }

    fun remove(coordinates: Coordinates, mask: Int) {
        val old = tiles[coordinates]?.flags ?: 0
        this[coordinates] = old and mask.inv()
    }

    operator fun get(coordinates: Coordinates): Int? {
        return tiles[coordinates]?.flags
    }

    operator fun set(coordinates: Coordinates, flags: Int) {
        tiles[coordinates] = CollisionTile(flags)
    }
}
