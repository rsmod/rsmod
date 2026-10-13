package org.runerealm.content.other.consumables

import jakarta.inject.Inject
import kotlin.math.max
import kotlin.math.min
import org.runerealm.api.config.refs.stats
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.stat.stat
import org.runerealm.api.player.stat.statBase
import org.runerealm.api.player.stat.statBoost
import org.runerealm.api.player.stat.statHeal
import org.runerealm.api.player.stat.statSub
import org.runerealm.api.player.vars.intVarp
import org.runerealm.api.script.onOpHeld1
import org.runerealm.api.script.onOpHeld2
import org.runerealm.content.other.consumables.configs.SIMPLE_FOOD_HEALS
import org.runerealm.content.other.consumables.configs.consumable_objs
import org.runerealm.content.other.consumables.configs.consumable_seqs
import org.runerealm.content.other.consumables.configs.consumable_varps
import org.runerealm.game.entity.Player
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.game.type.obj.UnpackedObjType
import org.runerealm.game.type.stat.StatType
import org.runerealm.game.type.stat.StatTypeList
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Eating food and drinking potions, following the OSRS Wiki:
 * - Most food delays the next bite and the next attack by 3 ticks. Cooked karambwan can be eaten in
 *   the same tick as other food and delays the next attack by 2 ticks.
 * - Potions have their own 3-tick delay and do not delay attacks.
 */
class ConsumablesScript
@Inject
constructor(private val objTypes: ObjTypeList, private val statTypes: StatTypeList) :
    PluginScript() {
    override fun ScriptContext.startup() {
        val foods = MULTI_BITE_FOODS.associateBy { it.type.id }.toMutableMap()
        for (type in objTypes.values) {
            val heal = SIMPLE_FOOD_HEALS[type.internalName] ?: continue
            foods.putIfAbsent(type.id, Food(type, heal))
        }
        for (food in foods.values) {
            val type = objTypes[food.type] ?: continue
            val op = type.eatOp() ?: continue
            if (op == 1) {
                onOpHeld1(food.type) { eat(it.slot, food) }
            } else {
                onOpHeld2(food.type) { eat(it.slot, food) }
            }
        }
        for (potion in POTIONS) {
            for ((index, dose) in potion.doses.withIndex()) {
                onOpHeld1(dose) { drink(it.slot, potion, index) }
            }
        }
    }

    private fun UnpackedObjType.eatOp(): Int? {
        val index = iop.indexOfFirst { it == "Eat" }
        return if (index in 0..1) index + 1 else null
    }

    private fun ProtectedAccess.eat(slot: Int, food: Food) {
        val delayVar = if (food.combo) consumable_varps.combo_delay else consumable_varps.food_delay
        if (player.vars[delayVar] > mapClock) {
            return
        }
        val obj = player.inv[slot] ?: return
        if (obj.id != food.type.id) {
            return
        }

        val replaced =
            if (food.next != null) {
                invReplaceSlot(player.inv, slot, 1, food.next).success
            } else {
                invDel(player.inv, food.type, count = 1, slot = slot).success
            }
        if (!replaced) {
            return
        }

        player.foodDelay = mapClock + FOOD_DELAY
        if (food.combo) {
            player.comboDelay = mapClock + FOOD_DELAY
        }
        val attackDelay = if (food.combo) COMBO_FOOD_ATTACK_DELAY else FOOD_DELAY
        player.actionDelay = max(player.actionDelay, mapClock) + attackDelay

        anim(consumable_seqs.eat)
        val name = objTypes[food.type]?.name?.lowercase() ?: "food"
        mes(food.message ?: "You eat the $name.")

        val before = player.stat(stats.hitpoints)
        player.restoreStat(stats.hitpoints, food.heal, 0)
        applyEffects(food.effects)
        if (player.stat(stats.hitpoints) > before) {
            mes("It heals some health.")
        }
    }

    private fun ProtectedAccess.drink(slot: Int, potion: Potion, doseIndex: Int) {
        if (player.potionDelay > mapClock) {
            return
        }
        val obj = player.inv[slot] ?: return
        if (obj.id != potion.doses[doseIndex].id) {
            return
        }
        val next = potion.doses.getOrNull(doseIndex + 1) ?: consumable_objs.vial_empty
        if (!invReplaceSlot(player.inv, slot, 1, next).success) {
            return
        }
        player.potionDelay = mapClock + POTION_DELAY

        anim(consumable_seqs.eat)
        mes("You drink some of your ${potion.name}.")
        applyEffects(potion.effects)

        when (val remaining = potion.doses.size - doseIndex - 1) {
            0 -> mes("You have finished your potion.")
            1 -> mes("You have 1 dose of potion left.")
            else -> mes("You have $remaining doses of potion left.")
        }
    }

    private fun ProtectedAccess.applyEffects(effects: List<ConsumeEffect>) {
        for (effect in effects) {
            when (effect) {
                is ConsumeEffect.Boost ->
                    player.statBoost(effect.stat, effect.constant, effect.percent)
                is ConsumeEffect.Restore ->
                    player.restoreStat(effect.stat, effect.constant, effect.percent)
                is ConsumeEffect.RestoreAllExceptHitpoints ->
                    for (stat in statTypes.values) {
                        if (stat.id != stats.hitpoints.id) {
                            player.restoreStat(stat, effect.constant, effect.percent)
                        }
                    }
                is ConsumeEffect.DrainCurrent -> {
                    val current = player.stat(effect.stat)
                    val drain = effect.constant + current * effect.percent / 100
                    player.statSub(effect.stat, drain, 0)
                }
                is ConsumeEffect.RunEnergy -> {
                    val restore = MAX_RUN_ENERGY * effect.percent / 100
                    player.runEnergy = min(MAX_RUN_ENERGY, player.runEnergy + restore)
                }
            }
        }
    }

    /** Heals [stat] towards its base level; a boosted stat is left unchanged. */
    private fun Player.restoreStat(stat: StatType, constant: Int, percent: Int) {
        if (stat(stat) < statBase(stat)) {
            statHeal(stat, constant, percent)
        }
    }
}

private var Player.foodDelay by intVarp(consumable_varps.food_delay)
private var Player.potionDelay by intVarp(consumable_varps.potion_delay)
private var Player.comboDelay by intVarp(consumable_varps.combo_delay)
