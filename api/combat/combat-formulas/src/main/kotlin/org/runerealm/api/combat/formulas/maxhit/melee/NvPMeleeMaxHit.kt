package org.runerealm.api.combat.formulas.maxhit.melee

import jakarta.inject.Inject
import org.runerealm.api.combat.commons.types.MeleeAttackType
import org.runerealm.api.combat.formulas.attributes.collector.DamageReductionAttributeCollector
import org.runerealm.api.combat.formulas.maxhit.MaxHitOperations
import org.runerealm.api.combat.maxhit.npc.NpcMeleeMaxHit
import org.runerealm.api.npc.meleeStrength
import org.runerealm.api.player.bonus.WornBonuses
import org.runerealm.api.random.GameRandom
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.Player

public class NvPMeleeMaxHit
@Inject
constructor(
    private val random: GameRandom,
    private val bonuses: WornBonuses,
    private val reductions: DamageReductionAttributeCollector,
) {
    public fun getMaxHit(npc: Npc, target: Player, attackType: MeleeAttackType?): Int {
        return computeMaxHit(npc, target, attackType)
    }

    public fun computeMaxHit(npc: Npc, target: Player, attackType: MeleeAttackType?): Int {
        val effectiveStrength = NpcMeleeMaxHit.calculateEffectiveStrength(npc.strengthLvl)
        val baseDamage = NpcMeleeMaxHit.calculateBaseDamage(effectiveStrength, npc.meleeStrength)

        val defenceBonus = target.getDefenceBonus(attackType)
        val reductionAttributes = reductions.collectNvP(target, random)
        return MaxHitOperations.applyDamageReductions(baseDamage, defenceBonus, reductionAttributes)
    }

    private fun Player.getDefenceBonus(attackType: MeleeAttackType?): Int =
        when (attackType) {
            MeleeAttackType.Stab -> bonuses.defensiveStabBonus(this)
            MeleeAttackType.Slash -> bonuses.defensiveSlashBonus(this)
            MeleeAttackType.Crush -> bonuses.defensiveCrushBonus(this)
            null -> 0
        }
}
