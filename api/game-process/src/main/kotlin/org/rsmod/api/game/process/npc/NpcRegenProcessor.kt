package org.rsmod.api.game.process.npc

import kotlin.math.sign
import org.rsmod.game.entity.Npc

public class NpcRegenProcessor {
    public fun process(npc: Npc) {
        npc.processStatsRegen()
    }

    private fun Npc.processStatsRegen() {
        if (regenRate == 0) {
            return
        }

        if (regenClock == 0) {
            regenClock = regenRate
            regenStats()
            return
        }

        regenClock--
    }

    private fun Npc.regenStats() {
        attackLvl = attackLvl.stepToward(baseAttackLvl)
        strengthLvl = strengthLvl.stepToward(baseStrengthLvl)
        defenceLvl = defenceLvl.stepToward(baseDefenceLvl)
        hitpoints = hitpoints.stepToward(baseHitpointsLvl)
        rangedLvl = rangedLvl.stepToward(baseRangedLvl)
        magicLvl = magicLvl.stepToward(baseMagicLvl)
    }

    private fun Int.stepToward(target: Int) = this + (target - this).sign
}
