package gg.rsmod.plugins.content.appearance

import gg.rsmod.game.model.appearance.Appearance
import gg.rsmod.plugins.api.model.mob.player.faceDirection
import gg.rsmod.plugins.api.model.mob.player.updateAppearance
import gg.rsmod.plugins.api.onEarlyLogin

onEarlyLogin {
    if (player.appearance === Appearance.ZERO) {
        player.appearance = AppearanceConstants.DEFAULT_APPEARANCE
    }
    player.updateAppearance()
    player.faceDirection(player.faceDirection)
}
