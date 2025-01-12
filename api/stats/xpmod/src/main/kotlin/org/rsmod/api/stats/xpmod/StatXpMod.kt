package org.rsmod.api.stats.xpmod

import org.rsmod.game.entity.Player
import org.rsmod.game.type.stat.StatType

abstract class StatXpMod(private val stat: StatType) : XpMod {
    abstract fun Player.modifier(): Double

    override fun Player.modifier(stat: StatType): Double {
        if (stat == this@StatXpMod.stat) {
            return modifier()
        }
        return 0.0
    }
}
