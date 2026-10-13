package org.runerealm.game.loc

import org.runerealm.routefinder.loc.LocLayerConstants

public enum class LocLayer(public val id: Int) {
    Wall(LocLayerConstants.WALL),
    WallDecor(LocLayerConstants.WALL_DECOR),
    Ground(LocLayerConstants.GROUND),
    GroundDecor(LocLayerConstants.GROUND_DECOR),
}
