package org.runerealm.api.combat.formulas.maxhit.melee

import org.runerealm.api.combat.maxhit.npc.NpcMeleeMaxHit
import org.runerealm.api.npc.meleeStrength
import org.runerealm.game.entity.Npc

public class NvNMeleeMaxHit {
    public fun getMaxHit(npc: Npc): Int = computeMaxHit(npc)

    public fun computeMaxHit(npc: Npc): Int {
        val effectiveStrength = NpcMeleeMaxHit.calculateEffectiveStrength(npc.strengthLvl)
        return NpcMeleeMaxHit.calculateBaseDamage(effectiveStrength, npc.meleeStrength)
    }
}
