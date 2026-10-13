package org.runerealm.api.hunt

import jakarta.inject.Inject
import org.runerealm.game.entity.Player
import org.runerealm.game.type.hunt.HuntVis
import org.runerealm.map.CoordGrid

public class PlayerSearch @Inject constructor(private val hunt: Hunt) {
    public fun findAll(coords: CoordGrid, maxDistance: Int, vis: HuntVis): Sequence<Player> {
        return hunt.findPlayers(coords, maxDistance, vis)
    }
}
