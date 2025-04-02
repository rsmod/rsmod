package org.rsmod.api.combat.formulas.maxhit.melee

import org.rsmod.api.combat.maxhit.npc.NpcMeleeMaxHit
import org.rsmod.api.npc.meleeStrength
import org.rsmod.game.entity.Npc

public class NvNMeleeMaxHit {
    public fun getMaxHit(npc: Npc): Int = computeMaxHit(npc)

    public fun computeMaxHit(npc: Npc): Int {
        val effectiveStrength = NpcMeleeMaxHit.calculateEffectiveStrength(npc.strengthLvl)
        return NpcMeleeMaxHit.calculateBaseDamage(effectiveStrength, npc.meleeStrength)
    }
}
