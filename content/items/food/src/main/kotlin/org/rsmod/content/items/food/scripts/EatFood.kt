package org.rsmod.content.items.food.scripts

import org.rsmod.api.config.refs.BaseStats.hitpoints
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.varps
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.baseHitpointsLvl
import org.rsmod.api.player.stat.hitpoints
import org.rsmod.api.player.stat.statAdd
import org.rsmod.api.player.stat.statBoost
import org.rsmod.api.player.stat.statHeal
import org.rsmod.api.player.vars.intVarp
import org.rsmod.api.script.onOpHeld1
import org.rsmod.content.items.food.configs.COMBO_FOOD_DELAY
import org.rsmod.content.items.food.configs.FOOD_DELAY
import org.rsmod.game.entity.Player
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class EatFood : PluginScript() {

    override fun ScriptContext.startup() {
        onOpHeld1(content.food) { this.eatFood(it.obj, it.type, it.slot) }

    }

    var Player.foodClock: Int by intVarp(varps.tracking_food_eaten)
    var Player.potionClock: Int by intVarp(varps.tracking_potions_sipped)

    private fun ProtectedAccess.eatFood(item: InvObj, type: UnpackedObjType, slot: Int) {
        if (!canEat(item, type, player)) {
            return
        }

        eat(item, type, slot)
        heal(item, type, player)
    }

    private fun ProtectedAccess.canEat(item: InvObj, type: UnpackedObjType, player: Player) : Boolean {
        return if (type.param(params.food_is_combo) == true && player.potionClock <= mapClock) {
            true
        } else player.foodClock <= mapClock
    }

    private fun ProtectedAccess.eat(item: InvObj, type: UnpackedObjType, slot: Int) {

        val delay = FOOD_DELAY
        val replacement = type.param(params.food_replacement)
        val name = ocName(type)

        if (type.param(params.food_is_combo) == true) {
            player.potionClock = mapClock + delay
            player.foodClock = mapClock + delay
        } else {
            player.foodClock = mapClock + delay
        }

        if (type.param(params.food_requires_replacement) == true) {
            invReplace(inv, item.id, 1, replacement)
        } else {
            invDel(inv, type, 1, slot)
        }

        anim(seqs.human_eat)
        spam("You eat the $name.")
    }

    private fun ProtectedAccess.heal(item: InvObj, type: UnpackedObjType, player: Player) {
        val healAmount = when (type.param(params.food_overheal) == true) {
            true -> anglerOverHeal(player)
            false -> type.param(params.food_heal_value)
        }
        if (type.param(params.food_overheal) == true) {
            player.statBoost(hitpoints, healAmount, 0)
        } else {
            if (player.baseHitpointsLvl < player.hitpoints) {
                return
            } else {
                player.statHeal(hitpoints, healAmount, 0)
            }
        }
    }



    private fun ProtectedAccess.anglerOverHeal(player: Player): Int {
        val x = player.baseHitpointsLvl
        return ((x / 10) + 2 * (x / 25) + 5 * (x / 93) + 2)
    }
}
