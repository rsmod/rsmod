package org.rsmod.api.testing.factory.npc

import org.rsmod.game.entity.Npc
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.map.CoordGrid

public class TestNpcFactory {
    public fun create(
        type: UnpackedNpcType,
        coords: CoordGrid = CoordGrid.ZERO,
        init: Npc.() -> Unit = {},
    ): Npc = Npc(type, coords).apply(init)
}
