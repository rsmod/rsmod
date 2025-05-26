package org.rsmod.content.items.food.configs

import org.rsmod.api.config.refs.BaseStats.hitpoints
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.queues
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.baseHitpointsLvl
import org.rsmod.api.player.stat.hitpoints
import org.rsmod.api.player.stat.statBoost
import org.rsmod.api.player.stat.statHeal
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.UnpackedObjType

fun ProtectedAccess.heal(type: UnpackedObjType, player: Player) {
    val healAmount =
        when (type.param(params.food_overheal) == true) {
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

fun ProtectedAccess.healOverTime(type: UnpackedObjType, player: Player) {

    val initialHeal = type.param(params.food_heal_value)
    val secondaryHeal = type.param(params.food_secondary_heal)

    if (player.baseHitpointsLvl < player.hitpoints) {
        return
    } else player.statHeal(hitpoints, initialHeal, 0)

    if (queues.food_secondary_heal_delay in player.queueList) {
        return
    } else {
        longQueueAccelerate(queues.food_secondary_heal_delay, 7, secondaryHeal)
    }
}

private fun ProtectedAccess.anglerOverHeal(player: Player): Int {
    val x = player.baseHitpointsLvl
    return ((x / 10) + 2 * (x / 25) + 5 * (x / 93) + 2)
}
