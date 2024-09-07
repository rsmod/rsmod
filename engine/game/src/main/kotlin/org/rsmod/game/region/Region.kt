package org.rsmod.game.region

import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneGrid
import org.rsmod.map.zone.ZoneKey

public class Region {
    public companion object {
        public const val START_COORD_X: Int = 6400
        public const val START_ZONE_X: Int = START_COORD_X / ZoneGrid.LENGTH

        public fun inWorkingArea(key: ZoneKey): Boolean = key.x >= START_ZONE_X

        public fun inWorkingArea(coords: CoordGrid): Boolean = coords.x >= START_COORD_X
    }
}
