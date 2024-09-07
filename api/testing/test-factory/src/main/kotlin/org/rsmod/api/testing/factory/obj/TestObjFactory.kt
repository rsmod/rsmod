package org.rsmod.api.testing.factory.obj

import org.rsmod.game.obj.Obj
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.map.CoordGrid

public class TestObjFactory {
    public fun create(
        type: UnpackedObjType,
        count: Int = 1,
        coords: CoordGrid = CoordGrid.ZERO,
        creationCycle: Int = 0,
        receiverId: Long? = null,
    ): Obj =
        if (receiverId != null) {
            Obj(coords, type, count = count, creationCycle = creationCycle, receiverId = receiverId)
        } else {
            Obj(coords, type, count = count, creationCycle = creationCycle)
        }
}
