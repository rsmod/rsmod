package org.runerealm.api.stats.xpmod

import jakarta.inject.Inject
import org.runerealm.game.entity.Player
import org.runerealm.game.type.stat.StatType

class XpModifiers @Inject constructor(private val mods: Set<XpMod>) {
    fun get(player: Player, stat: StatType): Double = 1.0 + mods.sumOf { it.modifier(player, stat) }

    private fun XpMod.modifier(player: Player, stat: StatType): Double = player.modifier(stat)
}
