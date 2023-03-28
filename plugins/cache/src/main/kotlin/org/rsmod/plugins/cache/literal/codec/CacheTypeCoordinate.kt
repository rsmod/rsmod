package org.rsmod.plugins.cache.literal.codec

import org.rsmod.game.map.Coordinates

public object CacheTypeCoordinate : CacheTypeBaseInt<Coordinates>(Coordinates::class.java) {

    override fun decode(value: Int): Coordinates {
        return Coordinates(value)
    }

    override fun encode(value: Coordinates): Int {
        return value.packed
    }
}
