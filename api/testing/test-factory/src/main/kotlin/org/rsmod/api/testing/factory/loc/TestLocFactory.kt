package org.rsmod.api.testing.factory.loc

import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.loc.LocAngle
import org.rsmod.game.loc.LocEntity
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.loc.LocShape
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.map.CoordGrid
import org.rsmod.routefinder.loc.LocLayerConstants

public class TestLocFactory {
    public fun create(
        coords: CoordGrid,
        angle: LocAngle = LocAngle.West,
        shape: LocShape = LocShape.CentrepieceStraight,
        id: Int = 0,
    ): LocInfo {
        val entity = LocEntity(id, shape.id, angle.id)
        val layer = LocLayerConstants.of(shape.id)
        return LocInfo(layer, coords, entity)
    }

    public fun create(
        type: UnpackedLocType,
        coords: CoordGrid = CoordGrid.ZERO,
        angle: LocAngle = LocAngle.West,
        shape: LocShape = LocShape.CentrepieceStraight,
    ): LocInfo = create(coords, angle, shape, type.id)

    public fun createBound(
        type: UnpackedLocType,
        coords: CoordGrid = CoordGrid.ZERO,
        angle: LocAngle = LocAngle.West,
        shape: LocShape = LocShape.CentrepieceStraight,
    ): BoundLocInfo {
        val entity = LocEntity(type.id, shape.id, angle.id)
        val layer = LocLayerConstants.of(shape.id)
        val loc = LocInfo(layer, coords, entity)
        return BoundLocInfo(loc, type)
    }
}
