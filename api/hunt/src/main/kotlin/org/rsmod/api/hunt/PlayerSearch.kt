package org.rsmod.api.hunt

import jakarta.inject.Inject
import org.rsmod.game.entity.Player
import org.rsmod.game.type.hunt.HuntVis
import org.rsmod.map.CoordGrid

public class PlayerSearch @Inject constructor(private val hunt: Hunt) {
    public fun findAll(coords: CoordGrid, maxDistance: Int, vis: HuntVis): Sequence<Player> {
        return hunt.findPlayers(coords, maxDistance, vis)
    }
}
