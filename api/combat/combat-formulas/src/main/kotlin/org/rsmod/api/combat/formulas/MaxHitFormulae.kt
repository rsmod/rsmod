package org.rsmod.api.combat.formulas

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.combat.formulas.maxhit.melee.NvPMeleeMaxHit
import org.rsmod.api.combat.formulas.maxhit.melee.PvNMeleeMaxHit
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player

public class MaxHitFormulae
@Inject
constructor(
    private val pvnMeleeMaxHit: PvNMeleeMaxHit,
    private val nvpMeleeMaxHit: NvPMeleeMaxHit,
) {
    public fun getMeleeMaxHit(player: Player, target: Npc, specMultiplier: Double = 1.0): Int {
        return pvnMeleeMaxHit.getMaxHit(player, target, specMultiplier)
    }

    public fun getMeleeMaxHit(npc: Npc, target: Player, attackType: MeleeAttackType?): Int {
        return nvpMeleeMaxHit.getMaxHit(npc, target, attackType)
    }
}
