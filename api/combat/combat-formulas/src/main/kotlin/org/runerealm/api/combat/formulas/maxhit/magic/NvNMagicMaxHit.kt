package org.runerealm.api.combat.formulas.maxhit.magic

import org.runerealm.api.combat.maxhit.npc.NpcMagicMaxHit
import org.runerealm.api.npc.magicStrength
import org.runerealm.game.entity.Npc

public class NvNMagicMaxHit {
    public fun getMaxHit(npc: Npc): Int = computeMaxHit(npc)

    public fun computeMaxHit(npc: Npc): Int {
        val effectiveMagic = NpcMagicMaxHit.calculateEffectiveMagic(npc.magicLvl)
        return NpcMagicMaxHit.calculateBaseDamage(effectiveMagic, npc.magicStrength)
    }
}
