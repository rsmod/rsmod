package org.rsmod.api.combat.formulas.maxhit.ranged

import jakarta.inject.Inject
import org.rsmod.api.combat.formulas.attributes.collector.DamageReductionAttributeCollector
import org.rsmod.api.combat.formulas.maxhit.MaxHitOperations
import org.rsmod.api.combat.maxhit.npc.NpcRangedMaxHit
import org.rsmod.api.npc.rangedStrength
import org.rsmod.api.player.bonus.WornBonuses
import org.rsmod.api.random.GameRandom
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player

public class NvPRangedMaxHit
@Inject
constructor(
    private val random: GameRandom,
    private val bonuses: WornBonuses,
    private val reductions: DamageReductionAttributeCollector,
) {
    public fun getMaxHit(npc: Npc, target: Player): Int {
        return computeMaxHit(npc, target)
    }

    public fun computeMaxHit(npc: Npc, target: Player): Int {
        val effectiveRanged = NpcRangedMaxHit.calculateEffectiveRanged(npc.rangedLvl)
        val baseDamage = NpcRangedMaxHit.calculateBaseDamage(effectiveRanged, npc.rangedStrength)

        val defenceBonus = bonuses.defensiveRangedBonus(target)
        val reductionAttributes = reductions.collectNvP(target, random)
        return MaxHitOperations.applyDamageReductions(baseDamage, defenceBonus, reductionAttributes)
    }
}
