package org.rsmod.api.game.process.player

import jakarta.inject.Inject
import kotlin.collections.iterator
import org.rsmod.api.registry.region.RegionRegistry
import org.rsmod.game.area.AreaIndex
import org.rsmod.game.entity.Player
import org.rsmod.map.CoordGrid

public class PlayerAreaProcessor
@Inject
constructor(private val index: AreaIndex, private val regions: RegionRegistry) {
    public fun process(player: Player) {
        player.processAreaChange()
    }

    private fun Player.processAreaChange() {
        // Area exit queues must be sent on the same cycle the player is queued to log out -
        // either via manual logout or forced disconnect.
        val forceExitAreas = pendingLogout || forceDisconnect

        val processAreas = forceExitAreas || coords != lastProcessedAreaCoord
        if (!processAreas) {
            return
        }
        lastProcessedAreaCoord = coords

        val normalizedCoords = normalizedCoords()
        pendingAreas.clear()
        if (!forceExitAreas) {
            index.putAreas(normalizedCoords, pendingAreas)
        }

        for (area in activeAreas.iterator()) {
            if (area in pendingAreas) {
                continue
            }
            engineQueueAreaExit(area)
        }

        for (area in pendingAreas.iterator()) {
            if (area in activeAreas) {
                continue
            }
            engineQueueArea(area)
        }

        activeAreas.clear()
        activeAreas.addAll(pendingAreas)
    }

    private fun Player.normalizedCoords(): CoordGrid =
        if (RegionRegistry.inWorkingArea(coords)) {
            regions.normalizeCoords(coords)
        } else {
            coords
        }
}
