package org.rsmod.api.game.process.player

import jakarta.inject.Inject
import org.rsmod.api.registry.region.RegionRegistry
import org.rsmod.game.entity.Player

public class PlayerRegionProcessor @Inject constructor(private val regionReg: RegionRegistry) {
    public fun process(player: Player) {
        player.assignRegionUid()
        player.assignLastKnownNormalCoord()
    }

    private fun Player.assignRegionUid() {
        val region = regionReg[coords]
        regionUid = region?.uid
    }

    private fun Player.assignLastKnownNormalCoord() {
        if (!RegionRegistry.inWorkingArea(coords)) {
            lastKnownNormalCoord = coords
        }
    }
}
