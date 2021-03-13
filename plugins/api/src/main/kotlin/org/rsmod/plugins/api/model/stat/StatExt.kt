package org.rsmod.plugins.api.model.stat

import org.rsmod.game.model.stat.Stat

fun Stat.baseLevel(): Int {
    return Stats.levelForExp(experience)
}

fun Stat.hasBaseLevel(minLevel: Int): Boolean {
    return baseLevel() >= minLevel
}

fun Stat.hasCurrLevel(minLevel: Int): Boolean {
    return currLevel >= minLevel
}
