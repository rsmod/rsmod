package org.rsmod.content.items.food.configs

import org.rsmod.api.config.refs.BaseStats.hitpoints
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.timers
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.baseHitpointsLvl
import org.rsmod.api.player.stat.hitpoints
import org.rsmod.api.player.stat.statBoost
import org.rsmod.api.player.stat.statHeal
import org.rsmod.api.player.vars.intVarp
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.UnpackedObjType

var Player.secondaryHealClock by intVarp(varps.tracking_secondary_heal)

 fun ProtectedAccess.heal(type: UnpackedObjType, player: Player) {
    val healAmount = when (type.param(params.food_overheal) == true) {
        true -> anglerOverHeal(player)
        false -> type.param(params.food_heal_value)
    }

    if (type.param(params.food_overheal)) {
        player.statBoost(hitpoints, healAmount, 0)

    }

    if (player.baseHitpointsLvl < player.hitpoints) {
        return
    } else player.statHeal(hitpoints, healAmount, 0)
}

suspend fun ProtectedAccess.healOverTime(type: UnpackedObjType, player: Player) {

    val initialHeal = type.param(params.food_heal_value)
    val secondaryHeal = type.param(params.food_secondary_heal)

    player.statHeal(hitpoints, initialHeal, 0)
    player.secondaryHealClock = mapClock + 7

    if (player.secondaryHealClock > mapClock) {
        return
    } else {
        delay(HEAL_OVER_TIME_DELAY)
        player.statHeal(hitpoints, secondaryHeal, 0)
    }
}

private fun ProtectedAccess.anglerOverHeal(player: Player): Int {
    val x = player.baseHitpointsLvl
    return ((x / 10) + 2 * (x / 25) + 5 * (x / 93) + 2)
}
