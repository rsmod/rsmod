package org.rsmod.api.game.process.player

import org.rsmod.api.utils.map.BuildAreaUtils
import org.rsmod.game.entity.Player
import org.rsmod.map.zone.ZoneKey

public class PlayerBuildAreaProcessor {
    public fun process(player: Player) {
        player.processBuildAreaChange()
    }

    private fun Player.processBuildAreaChange() {
        if (refreshBuildArea()) {
            enterBuildArea()
        }
    }

    private fun Player.refreshBuildArea(): Boolean {
        val dx = coords.x - buildArea.x
        val dz = coords.z - buildArea.z
        return dx < BuildAreaUtils.REBUILD_BOUNDARY ||
            dz < BuildAreaUtils.REBUILD_BOUNDARY ||
            dx >= BuildAreaUtils.SIZE - BuildAreaUtils.REBUILD_BOUNDARY ||
            dz >= BuildAreaUtils.SIZE - BuildAreaUtils.REBUILD_BOUNDARY
    }

    private fun Player.enterBuildArea() {
        buildArea = BuildAreaUtils.calculateBuildArea(ZoneKey.from(coords))
    }
}
