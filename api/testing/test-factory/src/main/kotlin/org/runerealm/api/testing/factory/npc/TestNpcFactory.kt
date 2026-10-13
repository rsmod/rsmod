package org.runerealm.api.testing.factory.npc

import org.runerealm.game.entity.Npc
import org.runerealm.game.type.npc.UnpackedNpcType
import org.runerealm.map.CoordGrid

public class TestNpcFactory {
    public fun create(
        type: UnpackedNpcType,
        coords: CoordGrid = CoordGrid.ZERO,
        init: Npc.() -> Unit = {},
    ): Npc = Npc(type, coords).apply(init)
}
