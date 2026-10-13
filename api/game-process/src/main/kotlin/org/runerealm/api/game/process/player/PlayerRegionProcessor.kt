package org.runerealm.api.game.process.player

import jakarta.inject.Inject
import org.runerealm.api.registry.region.RegionRegistry
import org.runerealm.game.entity.Player

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
