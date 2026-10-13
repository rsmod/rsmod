package org.runerealm.api.testing.factory.player

import org.runerealm.game.entity.Player
import org.runerealm.map.CoordGrid

public class TestPlayerFactory {
    public fun create(coords: CoordGrid = CoordGrid.ZERO, init: Player.() -> Unit = {}): Player =
        Player().apply { this.coords = coords }.apply(init)
}
