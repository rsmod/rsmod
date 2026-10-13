package org.runerealm.api.testing.factory.entity

import org.runerealm.game.entity.PathingEntity
import org.runerealm.game.entity.PathingEntityAvatar
import org.runerealm.game.entity.Player
import org.runerealm.game.entity.PlayerAvatar
import org.runerealm.map.CoordGrid

public class TestPathingEntityFactory {
    public fun create(
        coords: CoordGrid = CoordGrid.ZERO,
        init: PathingEntity.() -> Unit = {},
    ): PathingEntity = Player().apply { this.coords = coords }.apply(init)

    public fun createAvatar(
        coords: CoordGrid = CoordGrid.ZERO,
        init: PathingEntityAvatar.() -> Unit = {},
    ): PathingEntityAvatar = PlayerAvatar().apply { this.coords = coords }.apply(init)
}
