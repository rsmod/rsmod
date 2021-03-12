package org.rsmod.plugins.api.model.stat

import kotlin.math.floor
import kotlin.math.pow
import org.rsmod.game.model.stat.StatKey

object Stats {

    object Attack : StatKey(0)
    object Defence : StatKey(1)
    object Strength : StatKey(2)
    object Hitpoints : StatKey(3)
    object Ranged : StatKey(4)
    object Prayer : StatKey(5)
    object Magic : StatKey(6)
    object Cooking : StatKey(7)
    object Woodcutting : StatKey(8)
    object Fletching : StatKey(9)
    object Fishing : StatKey(10)
    object Firemaking : StatKey(11)
    object Crafting : StatKey(12)
    object Smithing : StatKey(13)
    object Mining : StatKey(14)
    object Herblore : StatKey(15)
    object Agility : StatKey(16)
    object Thieving : StatKey(17)
    object Slayer : StatKey(18)
    object Farming : StatKey(19)
    object Runecrafting : StatKey(20)
    object Hunter : StatKey(21)
    object Construction : StatKey(22)

    private const val MAX_STAT_LEVEL = 2000

    private val EXP_TABLE = IntArray(MAX_STAT_LEVEL).apply {
        var points = 0
        for (level in 1 until size) {
            points += floor(level + 300 * 2.0.pow(level / 7.0)).toInt()
            this[level] = points / 4
        }
    }

    val keys = mutableListOf(
        Attack,
        Defence,
        Strength,
        Hitpoints,
        Ranged,
        Prayer,
        Magic,
        Cooking,
        Woodcutting,
        Fletching,
        Fishing,
        Firemaking,
        Crafting,
        Smithing,
        Mining,
        Herblore,
        Agility,
        Thieving,
        Slayer,
        Farming,
        Runecrafting,
        Hunter,
        Construction
    )

    fun expForLevel(level: Int): Int {
        return EXP_TABLE[level - 1]
    }

    fun levelForExp(xp: Int): Int {
        return (1 until EXP_TABLE.size).firstOrNull { xp < it } ?: EXP_TABLE.last()
    }

    fun levelForExp(xp: Double) = levelForExp(xp.toInt())
}
