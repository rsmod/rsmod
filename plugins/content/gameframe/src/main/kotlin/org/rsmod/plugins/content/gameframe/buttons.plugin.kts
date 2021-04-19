package org.rsmod.plugins.content.gameframe

import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.move.MovementSpeed
import org.rsmod.game.model.vars.type.VarpTypeList
import org.rsmod.plugins.api.model.mob.player.getVarp
import org.rsmod.plugins.api.model.mob.player.sendMessage
import org.rsmod.plugins.api.model.mob.player.setVarp
import org.rsmod.plugins.api.model.mob.player.toggleVarp
import org.rsmod.plugins.api.onButton

val varps: VarpTypeList by inject()

val logout = component("logout_button")
val runOrb = component("run_orb_button")

val runVarp = varps[173]

onButton(logout) { player.logout() }
onButton(runOrb) { player.toggleRun() }

fun Player.toggleRun() {
    toggleVarp(runVarp)
    val newSpeed = getVarp(runVarp).toMovementSpeed()
    if (newSpeed == MovementSpeed.Run && runEnergy <= 0) {
        setVarp(runVarp, MovementSpeed.Walk.toVarp())
        sendMessage("You don't have enough energy left to run!")
        return
    }
    this.speed = newSpeed
}

fun MovementSpeed.toVarp(): Int = when (this) {
    MovementSpeed.Walk -> 0
    MovementSpeed.Run -> 1
}

fun Int.toMovementSpeed(): MovementSpeed = when (this) {
    1 -> MovementSpeed.Run
    else -> MovementSpeed.Walk
}
