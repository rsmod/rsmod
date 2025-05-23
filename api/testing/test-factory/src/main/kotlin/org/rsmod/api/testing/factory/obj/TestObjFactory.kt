package org.rsmod.api.testing.factory.obj

import org.rsmod.game.obj.Obj
import org.rsmod.game.obj.ObjEntity
import org.rsmod.game.obj.ObjScope
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
            val entity = ObjEntity(id = type.id, count = count, scope = ObjScope.Private.id)
            Obj(coords, entity, creationCycle, receiverId)
        } else {
            val entity = ObjEntity(id = type.id, count = count, scope = ObjScope.Temp.id)
            Obj(coords, entity, creationCycle, Obj.NULL_OBSERVER_ID)
        }
}
