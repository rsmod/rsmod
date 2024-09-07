package org.rsmod.api.testing.factory.entity

import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.PathingEntityAvatar
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerAvatar
import org.rsmod.map.CoordGrid

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
