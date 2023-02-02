package org.rsmod.plugins.api.cache.type.enums.literal

import org.rsmod.game.model.map.Coordinates

public object EnumTypeCoordinate : EnumTypeBaseInt<Coordinates> {

    override fun decode(value: Int): Coordinates {
        // TODO: correct decode
        return Coordinates(value)
    }
}
