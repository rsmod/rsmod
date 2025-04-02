package org.rsmod.api.combat.formulas.maxhit.magic

import org.rsmod.api.combat.maxhit.npc.NpcMagicMaxHit
import org.rsmod.api.npc.magicStrength
import org.rsmod.game.entity.Npc

public class NvNMagicMaxHit {
    public fun getMaxHit(npc: Npc): Int = computeMaxHit(npc)

    public fun computeMaxHit(npc: Npc): Int {
        val effectiveMagic = NpcMagicMaxHit.calculateEffectiveMagic(npc.magicLvl)
        return NpcMagicMaxHit.calculateBaseDamage(effectiveMagic, npc.magicStrength)
    }
}
