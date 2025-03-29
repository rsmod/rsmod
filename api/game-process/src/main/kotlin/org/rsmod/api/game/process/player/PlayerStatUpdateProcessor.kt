package org.rsmod.api.game.process.player

import jakarta.inject.Inject
import org.rsmod.api.player.output.UpdateStat
import org.rsmod.api.player.stat.stat
import org.rsmod.api.stats.levelmod.InvisibleLevels
import org.rsmod.game.entity.Player
import org.rsmod.game.type.stat.StatType
import org.rsmod.game.type.stat.StatTypeList

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
