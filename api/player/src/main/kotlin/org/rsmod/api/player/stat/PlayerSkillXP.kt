package org.rsmod.api.player.stat

import kotlin.math.min
import org.rsmod.api.stats.levelmod.InvisibleLevels
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.stat.PlayerSkillXPTable
import org.rsmod.game.stat.PlayerStatMap
import org.rsmod.game.type.stat.StatType

public object PlayerSkillXP {
    public fun internalAddXP(
        player: Player,
        stat: StatType,
        xp: Double,
        rate: Double,
        eventBus: EventBus,
        invisibleLevels: InvisibleLevels,
    ): Int = player.addXP(stat, xp, rate, eventBus, invisibleLevels)

    private fun Player.addXP(
        stat: StatType,
        xp: Double,
        rate: Double,
        eventBus: EventBus,
        invisibleLevels: InvisibleLevels,
    ): Int {
        val fineXp = PlayerStatMap.toFineXP(xp * rate)
        if (fineXp.isInfinite()) {
            throw IllegalArgumentException("Total XP being added is too high! (xp=$xp, rate=$rate)")
        }
        val addedFineXp = statMap.addXP(stat, fineXp)
        if (addedFineXp == 0) {
            // UpdateStat packet is sent even if stat is maxed.
            updateStat(stat, invisibleLevels)
            return 0
        }
        checkLevelUp(stat, eventBus)
        updateStat(stat, invisibleLevels)
        return PlayerStatMap.normalizeFineXP(addedFineXp)
    }

    private fun PlayerStatMap.addXP(stat: StatType, fineXp: Double): Int {
        val currXp = getFineXP(stat)
        val sumXp = min(PlayerStatMap.MAX_FINE_XP.toDouble(), currXp + fineXp).toInt()
        val addedXp = sumXp - currXp
        if (addedXp > 0) {
            setFineXP(stat, sumXp)
        }
        return addedXp
    }

    private fun Player.checkLevelUp(stat: StatType, eventBus: EventBus) {
        val baseLevel = statMap.getBaseLevel(stat)
        if (baseLevel >= stat.maxLevel) {
            return
        }
        val nextLevelXp = PlayerSkillXPTable.getXPFromLevel(baseLevel + 1)
        val currentXp = statMap.getXP(stat)
        if (currentXp >= nextLevelXp) {
            val newLevel = min(stat.maxLevel, PlayerSkillXPTable.getLevelFromXP(currentXp))
            statMap.setBaseLevel(stat, newLevel.toByte())

            val setCurrLevel = statMap.getCurrentLevel(stat) == baseLevel
            if (setCurrLevel) {
                statMap.setCurrentLevel(stat, newLevel.toByte())
            }

            val levelUp = AdvanceStatEvent(this, stat, baseLevel.toInt(), newLevel)
            eventBus.publish(levelUp)
        }
    }
}
