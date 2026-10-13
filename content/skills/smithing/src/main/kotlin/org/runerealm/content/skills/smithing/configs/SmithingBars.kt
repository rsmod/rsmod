package org.runerealm.content.skills.smithing.configs

import org.runerealm.game.type.comp.ComponentType
import org.runerealm.game.type.obj.ObjType

/**
 * A bar that can be smelted at a furnace.
 *
 * Levels, ores and experience are from the OSRS Wiki
 * ([Smithing#Smelting](https://oldschool.runescape.wiki/w/Smithing#Bar_chart)).
 *
 * @param ores the ores (and coal) consumed per bar.
 * @param anvilIndex the `smithing_bar_type` value of this bar on the anvil interface, or `0` if the
 *   bar cannot be smithed on an anvil.
 * @param anvilXp the experience per bar when smithing items on an anvil.
 */
data class SmithingBar(
    val bar: ObjType,
    val metal: String,
    val level: Int,
    val xp: Double,
    val ores: List<Pair<ObjType, Int>>,
    val anvilIndex: Int = 0,
    val anvilXp: Double = 0.0,
)

/** An item made on an anvil from [bar], shown on the `smithing` interface in [component]. */
data class SmithingProduct(val bar: SmithingBar, val component: ComponentType, val obj: ObjType)

object SmithingBars {
    private val objs = smithing_objs

    val bronze =
        SmithingBar(
            bar = objs.bronze_bar,
            metal = "bronze",
            level = 1,
            xp = 6.2,
            ores = listOf(objs.copper_ore to 1, objs.tin_ore to 1),
            anvilIndex = 1,
            anvilXp = 12.5,
        )

    val blurite =
        SmithingBar(
            bar = objs.blurite_bar,
            metal = "blurite",
            level = 13,
            xp = 8.0,
            ores = listOf(objs.blurite_ore to 1),
        )

    val iron =
        SmithingBar(
            bar = objs.iron_bar,
            metal = "iron",
            level = 15,
            xp = 12.5,
            ores = listOf(objs.iron_ore to 1),
            anvilIndex = 2,
            anvilXp = 25.0,
        )

    val silver =
        SmithingBar(
            bar = objs.silver_bar,
            metal = "silver",
            level = 20,
            xp = 13.7,
            ores = listOf(objs.silver_ore to 1),
        )

    val steel =
        SmithingBar(
            bar = objs.steel_bar,
            metal = "steel",
            level = 30,
            xp = 17.5,
            ores = listOf(objs.iron_ore to 1, objs.coal to 2),
            anvilIndex = 3,
            anvilXp = 37.5,
        )

    val gold =
        SmithingBar(
            bar = objs.gold_bar,
            metal = "gold",
            level = 40,
            xp = 22.5,
            ores = listOf(objs.gold_ore to 1),
        )

    val mithril =
        SmithingBar(
            bar = objs.mithril_bar,
            metal = "mithril",
            level = 50,
            xp = 30.0,
            ores = listOf(objs.mithril_ore to 1, objs.coal to 4),
            anvilIndex = 4,
            anvilXp = 50.0,
        )

    val adamantite =
        SmithingBar(
            bar = objs.adamantite_bar,
            metal = "adamantite",
            level = 70,
            xp = 37.5,
            ores = listOf(objs.adamantite_ore to 1, objs.coal to 6),
            anvilIndex = 5,
            anvilXp = 62.5,
        )

    val runite =
        SmithingBar(
            bar = objs.runite_bar,
            metal = "runite",
            level = 85,
            xp = 50.0,
            ores = listOf(objs.runite_ore to 1, objs.coal to 8),
            anvilIndex = 6,
            anvilXp = 75.0,
        )

    /** All smeltable bars, in the order the furnace interface lists them. */
    val all: List<SmithingBar> =
        listOf(bronze, blurite, iron, silver, steel, gold, mithril, adamantite, runite)

    /** Bars that can be smithed on an anvil, from lowest to highest tier. */
    val anvilBars: List<SmithingBar> = all.filter { it.anvilIndex > 0 }

    /** Gold bar experience while wearing goldsmith gauntlets. */
    const val GOLDSMITH_GAUNTLETS_XP: Double = 56.2

    /** Chance (out of 100) to refine iron ore without a ring of forging. */
    const val IRON_SUCCESS_PERCENT: Int = 50

    /** Charges in a ring of forging. */
    const val RING_OF_FORGING_CHARGES: Int = 140
}
