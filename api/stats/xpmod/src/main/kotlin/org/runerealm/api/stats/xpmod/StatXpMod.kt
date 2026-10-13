package org.runerealm.api.stats.xpmod

import org.runerealm.game.entity.Player
import org.runerealm.game.type.stat.StatType

abstract class StatXpMod(private val stat: StatType) : XpMod {
    abstract fun Player.modifier(): Double

    override fun Player.modifier(stat: StatType): Double {
        if (stat == this@StatXpMod.stat) {
            return modifier()
        }
        return 0.0
    }
}
