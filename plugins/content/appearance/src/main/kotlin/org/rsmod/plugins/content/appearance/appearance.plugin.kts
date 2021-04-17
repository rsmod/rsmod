package org.rsmod.plugins.content.appearance

import org.rsmod.game.model.domain.Appearance
import org.rsmod.game.model.domain.Direction
import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.model.mob.faceDirection
import org.rsmod.plugins.api.model.mob.player.updateAppearance
import org.rsmod.plugins.api.onEarlyLogin

onEarlyLogin {
    player.setAndUpdateAppearance()
    player.faceDirection(Direction.South)
}

fun Player.setAndUpdateAppearance() {
    if (entity.appearance === Appearance.ZERO) {
        entity.appearance = AppearanceConstants.DEFAULT_APPEARANCE
    }
    updateAppearance()
}
