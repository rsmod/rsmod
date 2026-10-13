package org.runerealm.api.combat.formulas.maxhit.ranged

import org.runerealm.api.combat.maxhit.npc.NpcRangedMaxHit
import org.runerealm.api.npc.rangedStrength
import org.runerealm.game.entity.Npc

public class NvNRangedMaxHit {
    public fun getMaxHit(npc: Npc): Int = computeMaxHit(npc)

    public fun computeMaxHit(npc: Npc): Int {
        val effectiveRanged = NpcRangedMaxHit.calculateEffectiveRanged(npc.rangedLvl)
        return NpcRangedMaxHit.calculateBaseDamage(effectiveRanged, npc.rangedStrength)
    }
}
