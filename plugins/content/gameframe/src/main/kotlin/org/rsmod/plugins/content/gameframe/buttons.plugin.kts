package org.rsmod.plugins.content.gameframe

import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.step.StepSpeed
import org.rsmod.game.model.ui.Component
import org.rsmod.plugins.api.onButton

val logout = Component(182, 8)
val runOrb = Component(160, 22)

onButton(logout) { player.logout() }
onButton(runOrb) { player.toggleRun() }

fun Player.toggleRun() {
    // TODO: disable running when no energy
    speed = if (speed == StepSpeed.Walk) {
        StepSpeed.Run
    } else {
        StepSpeed.Walk
    }
}
