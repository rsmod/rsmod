package org.rsmod.plugins.content.gameframe

import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.move.MovementSpeed
import org.rsmod.game.model.ui.Component
import org.rsmod.plugins.api.model.mob.player.sendMessage
import org.rsmod.plugins.api.onButton

val logout = Component(182, 8)
val runOrb = Component(160, 22)

onButton(logout) { player.logout() }
onButton(runOrb) { player.toggleRun() }

fun Player.toggleRun() {
    speed = if (speed == MovementSpeed.Walk) {
        MovementSpeed.Run
    } else {
        MovementSpeed.Walk
    }
    if (speed == MovementSpeed.Run && runEnergy <= 0) {
        speed = MovementSpeed.Walk
        sendMessage("You don't have enough energy left to run!")
    }
}
