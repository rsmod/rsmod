package org.rsmod.game.hero

import kotlin.math.min

public class HeroPoints private constructor(private val heroes: Array<Hero?>) {
    public constructor(size: Int) : this(arrayOfNulls(size))

    public fun clear() {
        heroes.fill(null)
    }

    public fun add(uuid: Long, points: Int) {
        require(points > 0) { "`points` must be greater than 0: $points (uuid=$uuid)" }

        val hero = heroes.firstOrNull { it?.uuid == uuid }
        if (hero != null) {
            val sum = hero.points + points.toLong()
            hero.points = min(Int.MAX_VALUE.toLong(), sum).toInt()
            return
        }

        val emptyIndex = heroes.indexOfFirst { it == null }
        if (emptyIndex != -1) {
            heroes[emptyIndex] = Hero(uuid, points)
        }
    }

    public fun toMutableList(): MutableList<Hero> {
        // `count` should be slightly faster than dynamically resizing `list`.
        val count = heroes.count { it != null }
        val list = ArrayList<Hero>(count)
        for (hero in heroes) {
            if (hero != null) {
                list += hero
            }
        }
        return list
    }

    public data class Hero(val uuid: Long, var points: Int)
}
