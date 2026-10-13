package org.runerealm.content.other.consumables

import org.runerealm.api.config.constants
import org.runerealm.api.config.refs.stats
import org.runerealm.content.other.consumables.configs.consumable_objs as objs
import org.runerealm.game.type.obj.ObjType
import org.runerealm.game.type.stat.StatType

/** An effect applied when a food or potion is consumed. */
sealed interface ConsumeEffect {
    /** Temporarily raises [stat] by `constant + percent% of base`, capped at that boost. */
    data class Boost(val stat: StatType, val constant: Int, val percent: Int = 0) : ConsumeEffect

    /** Restores a lowered [stat] by `constant + percent% of base`, up to its base level. */
    data class Restore(val stat: StatType, val constant: Int, val percent: Int = 0) : ConsumeEffect

    /** Lowers [stat] by `constant + percent% of the current level` (Saradomin brew). */
    data class DrainCurrent(val stat: StatType, val constant: Int, val percent: Int) :
        ConsumeEffect

    /** Restores every stat except Hitpoints by `constant + percent% of base` (super restore). */
    data class RestoreAllExceptHitpoints(val constant: Int, val percent: Int) : ConsumeEffect

    /** Restores [percent]% run energy. */
    data class RunEnergy(val percent: Int) : ConsumeEffect
}

/**
 * A food item. [next] is what remains after this bite (the next slice, a pie dish), or `null` when
 * nothing remains. [message] is the chat message shown when eaten; `null` uses "You eat the
 * [name]."
 */
data class Food(
    val type: ObjType,
    val heal: Int,
    val next: ObjType? = null,
    val message: String? = null,
    val effects: List<ConsumeEffect> = emptyList(),
    val combo: Boolean = false,
)

/** A potion series, highest dose first; the last dose leaves an empty vial. */
data class Potion(val name: String, val doses: List<ObjType>, val effects: List<ConsumeEffect>)

/**
 * Multi-bite foods from the OSRS Wiki
 * [Food/All food](https://oldschool.runescape.wiki/w/Food/All_food) table, with the stat boosts
 * listed in its notes.
 */
internal val MULTI_BITE_FOODS: List<Food> = buildList {
    fun cake(whole: ObjType, partial: ObjType, slice: ObjType, heal: Int, name: String) {
        add(Food(whole, heal, partial, "You eat part of the $name."))
        add(Food(partial, heal, slice, "You eat some more $name."))
        add(Food(slice, heal, null, "You eat the slice of $name."))
    }
    cake(objs.cake, objs.partial_cake, objs.cake_slice, 4, "cake")
    cake(
        objs.chocolate_cake,
        objs.partial_chocolate_cake,
        objs.chocolate_slice,
        5,
        "chocolate cake",
    )

    fun pizza(whole: ObjType, half: ObjType, heal: Int, name: String) {
        add(Food(whole, heal, half, "You eat half of the $name."))
        add(Food(half, heal, null, "You eat the remaining $name."))
    }
    pizza(objs.plain_pizza, objs.half_plain_pizza, 7, "plain pizza")
    pizza(objs.meat_pizza, objs.half_meat_pizza, 8, "meat pizza")
    pizza(objs.anchovy_pizza, objs.half_anchovy_pizza, 9, "anchovy pizza")
    pizza(objs.pineapple_pizza, objs.half_pineapple_pizza, 11, "pineapple pizza")

    fun pie(whole: ObjType, half: ObjType, heal: Int, name: String, vararg effects: ConsumeEffect) {
        add(Food(whole, heal, half, "You eat half of the $name.", effects.toList()))
        add(Food(half, heal, objs.pie_dish, "You eat the remaining $name.", effects.toList()))
    }
    pie(objs.apple_pie, objs.half_apple_pie, 7, "apple pie")
    pie(objs.redberry_pie, objs.half_redberry_pie, 5, "redberry pie")
    pie(objs.meat_pie, objs.half_meat_pie, 6, "meat pie")
    pie(objs.garden_pie, objs.half_garden_pie, 6, "garden pie", boost(stats.farming, 3))
    pie(objs.fish_pie, objs.half_fish_pie, 6, "fish pie", boost(stats.fishing, 3))
    pie(objs.admiral_pie, objs.half_admiral_pie, 8, "admiral pie", boost(stats.fishing, 5))
    pie(
        objs.wild_pie,
        objs.half_wild_pie,
        11,
        "wild pie",
        boost(stats.slayer, 5),
        boost(stats.ranged, 4),
    )
    pie(
        objs.summer_pie,
        objs.half_summer_pie,
        11,
        "summer pie",
        boost(stats.agility, 5),
        ConsumeEffect.RunEnergy(10),
    )
    pie(objs.botanical_pie, objs.half_botanical_pie, 7, "botanical pie", boost(stats.herblore, 4))
    pie(objs.mushroom_pie, objs.half_mushroom_pie, 8, "mushroom pie", boost(stats.crafting, 4))
    pie(
        objs.dragonfruit_pie,
        objs.half_dragonfruit_pie,
        10,
        "dragonfruit pie",
        boost(stats.fletching, 4),
    )

    // Cooked karambwan: heals 18 and can be combo-eaten in the same tick as other food.
    add(Food(objs.karambwan, 18, combo = true))
}

/** Potion effects from the OSRS Wiki [Potions](https://oldschool.runescape.wiki/w/Potions) page. */
internal val POTIONS: List<Potion> =
    listOf(
        Potion(
            "attack potion",
            listOf(
                objs.attack_potion4,
                objs.attack_potion3,
                objs.attack_potion2,
                objs.attack_potion1,
            ),
            listOf(boost(stats.attack, 3, 10)),
        ),
        Potion(
            "strength potion",
            listOf(
                objs.strength_potion4,
                objs.strength_potion3,
                objs.strength_potion2,
                objs.strength_potion1,
            ),
            listOf(boost(stats.strength, 3, 10)),
        ),
        Potion(
            "defence potion",
            listOf(
                objs.defence_potion4,
                objs.defence_potion3,
                objs.defence_potion2,
                objs.defence_potion1,
            ),
            listOf(boost(stats.defence, 3, 10)),
        ),
        Potion(
            "super attack potion",
            listOf(objs.super_attack4, objs.super_attack3, objs.super_attack2, objs.super_attack1),
            listOf(boost(stats.attack, 5, 15)),
        ),
        Potion(
            "super strength potion",
            listOf(
                objs.super_strength4,
                objs.super_strength3,
                objs.super_strength2,
                objs.super_strength1,
            ),
            listOf(boost(stats.strength, 5, 15)),
        ),
        Potion(
            "super defence potion",
            listOf(
                objs.super_defence4,
                objs.super_defence3,
                objs.super_defence2,
                objs.super_defence1,
            ),
            listOf(boost(stats.defence, 5, 15)),
        ),
        Potion(
            "ranging potion",
            listOf(
                objs.ranging_potion4,
                objs.ranging_potion3,
                objs.ranging_potion2,
                objs.ranging_potion1,
            ),
            listOf(boost(stats.ranged, 4, 10)),
        ),
        Potion(
            "magic potion",
            listOf(objs.magic_potion4, objs.magic_potion3, objs.magic_potion2, objs.magic_potion1),
            listOf(boost(stats.magic, 4)),
        ),
        Potion(
            "combat potion",
            listOf(
                objs.combat_potion4,
                objs.combat_potion3,
                objs.combat_potion2,
                objs.combat_potion1,
            ),
            listOf(boost(stats.attack, 3, 10), boost(stats.strength, 3, 10)),
        ),
        Potion(
            "prayer potion",
            listOf(
                objs.prayer_potion4,
                objs.prayer_potion3,
                objs.prayer_potion2,
                objs.prayer_potion1,
            ),
            listOf(restore(stats.prayer, 7, 25)),
        ),
        Potion(
            "restore potion",
            listOf(
                objs.restore_potion4,
                objs.restore_potion3,
                objs.restore_potion2,
                objs.restore_potion1,
            ),
            listOf(stats.attack, stats.strength, stats.defence, stats.ranged, stats.magic).map {
                restore(it, 10, 30)
            },
        ),
        Potion(
            "super restore potion",
            listOf(
                objs.super_restore4,
                objs.super_restore3,
                objs.super_restore2,
                objs.super_restore1,
            ),
            listOf(ConsumeEffect.RestoreAllExceptHitpoints(8, 25)),
        ),
        Potion(
            "energy potion",
            listOf(
                objs.energy_potion4,
                objs.energy_potion3,
                objs.energy_potion2,
                objs.energy_potion1,
            ),
            listOf(ConsumeEffect.RunEnergy(15)),
        ),
        Potion(
            "super energy potion",
            listOf(objs.super_energy4, objs.super_energy3, objs.super_energy2, objs.super_energy1),
            listOf(ConsumeEffect.RunEnergy(20)),
        ),
        Potion(
            "saradomin brew",
            listOf(
                objs.saradomin_brew4,
                objs.saradomin_brew3,
                objs.saradomin_brew2,
                objs.saradomin_brew1,
            ),
            // Wiki: raises Hitpoints by 15% + 2 and Defence by 20% + 2 of base levels, and lowers
            // Attack, Strength, Magic and Ranged by 10% + 2 of their current levels.
            listOf(
                boost(stats.hitpoints, 2, 15),
                boost(stats.defence, 2, 20),
                ConsumeEffect.DrainCurrent(stats.attack, 2, 10),
                ConsumeEffect.DrainCurrent(stats.strength, 2, 10),
                ConsumeEffect.DrainCurrent(stats.magic, 2, 10),
                ConsumeEffect.DrainCurrent(stats.ranged, 2, 10),
            ),
        ),
    )

internal const val FOOD_DELAY = 3
internal const val COMBO_FOOD_ATTACK_DELAY = 2
internal const val POTION_DELAY = 3
internal const val MAX_RUN_ENERGY = constants.run_max_energy

private fun boost(stat: StatType, constant: Int, percent: Int = 0) =
    ConsumeEffect.Boost(stat, constant, percent)

private fun restore(stat: StatType, constant: Int, percent: Int = 0) =
    ConsumeEffect.Restore(stat, constant, percent)
