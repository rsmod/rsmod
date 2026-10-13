package org.runerealm.api.game.process.player

import jakarta.inject.Inject
import org.runerealm.api.player.output.UpdateStat
import org.runerealm.api.player.stat.stat
import org.runerealm.api.stats.levelmod.InvisibleLevels
import org.runerealm.game.entity.Player
import org.runerealm.game.type.stat.StatType
import org.runerealm.game.type.stat.StatTypeList

public class PlayerStatUpdateProcessor
@Inject
constructor(private val statTypes: StatTypeList, private val invisibleLevels: InvisibleLevels) {
    public fun process(player: Player) {
        if (player.pendingStatUpdates.isEmpty) {
            return
        }
        player.updatePendingStats()
    }

    private fun Player.updatePendingStats() {
        var nextStat = pendingStatUpdates.nextSetBit(0)
        while (nextStat >= 0) {
            updateStatXp(statTypes.getValue(nextStat))
            nextStat = pendingStatUpdates.nextSetBit(nextStat + 1)
        }
        pendingStatUpdates.clear()
    }

    private fun Player.updateStatXp(stat: StatType) {
        val currXp = statMap.getXP(stat)
        val currLvl = stat(stat)
        val hiddenLevel = currLvl + invisibleLevels.get(this, stat)
        UpdateStat.update(this, stat, currXp, currLvl, hiddenLevel)
    }
}
