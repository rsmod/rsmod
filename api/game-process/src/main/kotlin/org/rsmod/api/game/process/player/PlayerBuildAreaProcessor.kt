package org.rsmod.api.game.process.player

import org.rsmod.api.utils.map.BuildAreaUtils
import org.rsmod.game.entity.Player
import org.rsmod.map.zone.ZoneKey

public class PlayerBuildAreaProcessor {
    public fun process(player: Player) {
        player.processBuildAreaChange()
    }

    private fun Player.processBuildAreaChange() {
        val rebuildBuildArea = BuildAreaUtils.requiresNewBuildArea(this)
        if (rebuildBuildArea) {
            enterBuildArea()
        }
    }

    private fun Player.enterBuildArea() {
        buildArea = BuildAreaUtils.calculateBuildArea(ZoneKey.from(coords))
    }
}
