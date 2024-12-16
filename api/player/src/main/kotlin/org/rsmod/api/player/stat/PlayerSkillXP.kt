package org.rsmod.api.player.stat

import kotlin.math.min
import org.rsmod.api.player.output.UpdateStat
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.stat.PlayerSkillXPTable
import org.rsmod.game.stat.PlayerStatMap
import org.rsmod.game.type.stat.StatType

public object PlayerSkillXP {
    // TODO: Move MAX_LEVEL as a value within each StatType.
    private const val MAX_LEVEL: Int = 99

    public fun internalAddXP(
        player: Player,
        stat: StatType,
        xp: Double,
        rate: Double,
        eventBus: EventBus,
    ): Int = player.addXP(stat, xp, rate, eventBus)

    private fun Player.addXP(stat: StatType, xp: Double, rate: Double, eventBus: EventBus): Int {
        val fineXp = PlayerStatMap.toFineXP(xp * rate)
        if (fineXp.isInfinite()) {
            throw IllegalArgumentException("Total XP being added is too high! (xp=$xp, rate=$rate)")
        }
        val addedFineXp = statMap.addXP(stat, fineXp)
        if (addedFineXp == 0) {
            // UpdateStat packet is sent even if stat is maxed.
            updateStat(stat)
            return 0
        }
        checkLevelUp(stat, eventBus)
        updateStat(stat)
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
        if (baseLevel >= MAX_LEVEL) {
            return
        }
        val nextLevelXp = PlayerSkillXPTable.getXPFromLevel(baseLevel + 1)
        val currentXp = statMap.getXP(stat)
        if (currentXp >= nextLevelXp) {
            val newLevel = min(MAX_LEVEL, PlayerSkillXPTable.getLevelFromXP(currentXp))
            statMap.setBaseLevel(stat, newLevel.toByte())

            // TODO: Find out if condition should return true when current level is boosted.
            val setCurrLevel = statMap.getCurrentLevel(stat) == baseLevel
            if (setCurrLevel) {
                statMap.setCurrentLevel(stat, newLevel.toByte())
            }

            val levelUp = AdvanceStatEvent(this, stat, baseLevel.toInt(), newLevel.toInt())
            eventBus.publish(levelUp)
        }
    }

    private fun Player.updateStat(stat: StatType) {
        val currXp = statMap.getXP(stat)
        val currLvl = statMap.getCurrentLevel(stat).toInt()
        UpdateStat.update(this, stat, currXp, currLvl, currLvl)
    }
}
