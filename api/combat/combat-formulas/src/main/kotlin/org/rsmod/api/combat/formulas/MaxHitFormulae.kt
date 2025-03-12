package org.rsmod.api.combat.formulas

import jakarta.inject.Inject
import org.rsmod.api.combat.formulas.maxhit.MeleeMaxHit
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player

public class MaxHitFormulae @Inject constructor(private val meleeMaxHit: MeleeMaxHit) {
    public fun getMeleeMaxHit(player: Player, target: Npc, specMultiplier: Double = 1.0): Int {
        return meleeMaxHit.getMaxHit(player, target, specMultiplier)
    }

    public fun getMeleeMaxHit(npc: Npc): Int {
        return meleeMaxHit.getMaxHit(npc)
    }
}
