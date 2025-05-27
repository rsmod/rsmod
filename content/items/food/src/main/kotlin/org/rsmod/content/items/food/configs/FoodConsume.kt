package org.rsmod.content.items.food.configs

import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.vars.intVarp
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.InvObj
import org.rsmod.game.type.obj.UnpackedObjType

var Player.foodClock: Int by intVarp(varps.tracking_food_eaten)
var Player.potionClock: Int by intVarp(varps.tracking_potions_sipped)

fun ProtectedAccess.eat(item: InvObj, type: UnpackedObjType, slot: Int) {

    val replacement = type.param(params.food_replacement)
    val name = ocName(type)

    if (type.param(params.food_is_combo)) {
        player.potionClock = mapClock + FOOD_DELAY
        player.foodClock = mapClock + FOOD_DELAY
    } else player.foodClock = mapClock + FOOD_DELAY

    if (type.param(params.food_requires_replacement)) {
        invReplace(inv, item.id, 1, replacement)
    } else invDel(inv, type, 1, slot)

    anim(seqs.human_eat)
    spam("You eat the $name.")
}

fun ProtectedAccess.canEat(type: UnpackedObjType, player: Player): Boolean {
    return if (type.param(params.food_is_combo) && player.potionClock <= mapClock) {
        true
    } else player.foodClock <= mapClock
}

fun ProtectedAccess.boost(type: UnpackedObjType, player: Player) {
    if (type.hasParam(params.boosted_skill1)) {
        statBoost(type.param(params.boosted_skill1), type.param(params.boosted_skill1_value), 0)
    }

    if (type.hasParam(params.boosted_skill2)) {
        statBoost(type.param(params.boosted_skill2), type.param(params.boosted_skill2_value), 0)
    }

    if (type.hasParam(params.boosted_skill3)) {
        statBoost(type.param(params.boosted_skill3), type.param(params.boosted_skill3_value), 0)
    }
}
