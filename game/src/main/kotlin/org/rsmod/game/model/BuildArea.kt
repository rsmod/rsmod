package org.rsmod.game.model

import org.rsmod.game.map.Coordinates

@JvmInline
public value class BuildArea(public val base: Coordinates) {

    public companion object {

        public val ZERO: BuildArea = BuildArea(Coordinates.ZERO)
    }
}
