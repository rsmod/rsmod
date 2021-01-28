package org.rsmod.plugins.content.gameframe

import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.step.StepSpeed
import org.rsmod.game.model.ui.Component
import org.rsmod.plugins.api.model.mob.player.sendMessage
import org.rsmod.plugins.api.onButton

val logout = Component(182, 8)
val runOrb = Component(160, 22)

onButton(logout) { player.logout() }
onButton(runOrb) { player.toggleRun() }

fun Player.toggleRun() {
    speed = if (speed == StepSpeed.Walk) {
        StepSpeed.Run
    } else {
        StepSpeed.Walk
    }
    if (speed == StepSpeed.Run && runEnergy <= 0) {
        speed = StepSpeed.Walk
        sendMessage("You don't have enough energy left to run!")
    }
}
