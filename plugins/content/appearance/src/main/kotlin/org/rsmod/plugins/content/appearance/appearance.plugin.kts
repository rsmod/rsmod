package org.rsmod.plugins.content.appearance

import org.rsmod.game.model.domain.Appearance
import org.rsmod.plugins.api.model.mob.player.faceDirection
import org.rsmod.plugins.api.model.mob.player.updateAppearance
import org.rsmod.plugins.api.onEarlyLogin

onEarlyLogin {
    if (player.appearance === Appearance.ZERO) {
        player.appearance = AppearanceConstants.DEFAULT_APPEARANCE
    }
    player.updateAppearance()
    player.faceDirection(player.faceDirection)
}
