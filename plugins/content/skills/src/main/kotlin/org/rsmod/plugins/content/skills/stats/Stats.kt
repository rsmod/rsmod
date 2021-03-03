package org.rsmod.plugins.content.skills.stats

import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.stat.Stat
import org.rsmod.game.model.stat.StatMap
import org.rsmod.plugins.api.model.mob.player.setXp
import org.rsmod.plugins.api.model.stat.Stats

internal object Stats {

    private const val DEFAULT_STAT_LEVEL = 1

    private val DEFAULT_STAT_LEVELS = mapOf(
        Stats.Hitpoints to 10
    )

    fun setDefaultStats(stats: StatMap) {
        Stats.KEYS.forEach { key ->
            val level = DEFAULT_STAT_LEVELS[key] ?: DEFAULT_STAT_LEVEL
            val exp = Stats.expForLevel(level)
            stats[key] = Stat(level, exp.toDouble())
        }
    }

    fun refreshStats(player: Player) {
        player.stats.forEach { (key, stat) ->
            player.setXp(stat, key, stat.experience)
        }
    }
}
