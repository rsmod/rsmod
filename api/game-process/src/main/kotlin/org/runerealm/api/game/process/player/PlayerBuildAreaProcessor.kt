package org.runerealm.api.game.process.player

import org.runerealm.api.utils.map.BuildAreaUtils
import org.runerealm.game.entity.Player
import org.runerealm.map.zone.ZoneKey

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
