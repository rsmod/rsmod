package org.runerealm.api.combat.formulas.maxhit.ranged

import jakarta.inject.Inject
import org.runerealm.api.combat.formulas.attributes.collector.DamageReductionAttributeCollector
import org.runerealm.api.combat.formulas.maxhit.MaxHitOperations
import org.runerealm.api.combat.maxhit.npc.NpcRangedMaxHit
import org.runerealm.api.npc.rangedStrength
import org.runerealm.api.player.bonus.WornBonuses
import org.runerealm.api.random.GameRandom
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.Player

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
