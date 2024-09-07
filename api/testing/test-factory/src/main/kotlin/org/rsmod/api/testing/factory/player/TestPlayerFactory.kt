package org.rsmod.api.testing.factory.player

import org.rsmod.game.entity.Player
import org.rsmod.map.CoordGrid

public class TestPlayerFactory {
    public fun create(coords: CoordGrid = CoordGrid.ZERO, init: Player.() -> Unit = {}): Player =
        Player().apply { this.coords = coords }.apply(init)
}
