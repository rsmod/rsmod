package org.rsmod.api.combat.formulas.maxhit.ranged

import org.rsmod.api.combat.maxhit.npc.NpcRangedMaxHit
import org.rsmod.api.npc.rangedStrength
import org.rsmod.game.entity.Npc

public class NvNRangedMaxHit {
    public fun getMaxHit(npc: Npc): Int = computeMaxHit(npc)

    public fun computeMaxHit(npc: Npc): Int {
        val effectiveRanged = NpcRangedMaxHit.calculateEffectiveRanged(npc.rangedLvl)
        return NpcRangedMaxHit.calculateBaseDamage(effectiveRanged, npc.rangedStrength)
    }
}
