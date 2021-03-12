package org.rsmod.plugins.content.skills

import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.stat.Stat
import org.rsmod.game.model.stat.StatMap
import org.rsmod.plugins.api.model.mob.player.sendStat
import org.rsmod.plugins.api.model.stat.Stats

internal object StatPlugin {

    private const val DEFAULT_STAT_LEVEL = 1

    private val DEFAULT_STAT_LEVELS = mapOf(
        Stats.Hitpoints to 10
    )

    fun setDefaultStats(stats: StatMap) {
        Stats.keys.forEach { key ->
            val level = DEFAULT_STAT_LEVELS[key] ?: DEFAULT_STAT_LEVEL
            val exp = Stats.expForLevel(level)
            stats[key] = Stat(level, exp.toDouble())
        }
    }

    fun refreshStats(player: Player) {
        player.stats.forEach { (key, stat) ->
            player.sendStat(key, stat)
        }
    }
}
