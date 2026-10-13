package org.runerealm.api.testing.factory.loc

import org.runerealm.game.loc.BoundLocInfo
import org.runerealm.game.loc.LocAngle
import org.runerealm.game.loc.LocEntity
import org.runerealm.game.loc.LocInfo
import org.runerealm.game.loc.LocShape
import org.runerealm.game.type.loc.UnpackedLocType
import org.runerealm.map.CoordGrid
import org.runerealm.routefinder.loc.LocLayerConstants

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
