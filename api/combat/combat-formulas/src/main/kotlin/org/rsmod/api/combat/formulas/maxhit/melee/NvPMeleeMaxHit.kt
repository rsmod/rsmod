package org.rsmod.api.combat.formulas.maxhit.melee

import org.rsmod.api.combat.maxhit.npc.NpcMeleeMaxHit
import org.rsmod.api.config.refs.params
import org.rsmod.game.entity.Npc

public class NvPMeleeMaxHit {
    public fun getMaxHit(npc: Npc): Int {
        val effectiveStrength = calculateEffectiveStrength(npc)
        // The melee strength is extracted from the `visType` to account for transmog
        // changes. This is a bit obscure, but hopefully the toplevel `Npc.meleeStrength`
        // property helps. We do not depend on the `npc` module here, so we cannot call it.
        val strengthBonus = npc.visType.param(params.melee_strength)
        return NpcMeleeMaxHit.calculateBaseDamage(effectiveStrength, strengthBonus)
    }

    private fun calculateEffectiveStrength(npc: Npc): Int {
        return NpcMeleeMaxHit.calculateEffectiveStrength(npc.strengthLvl)
    }
}
