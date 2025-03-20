package org.rsmod.api.combat.formulas.maxhit.melee

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.combat.formulas.attributes.collector.DamageReductionAttributeCollector
import org.rsmod.api.combat.maxhit.npc.NpcMeleeMaxHit
import org.rsmod.api.npc.meleeStrength
import org.rsmod.api.player.bonus.WornBonuses
import org.rsmod.api.random.GameRandom
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player

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
        val reductionAttributes = reductions.collect(target, pvp = false, random)
        return MeleeMaxHitOperations.applyDamageReductions(
            baseDamage,
            defenceBonus,
            reductionAttributes,
        )
    }

    private fun Player.getDefenceBonus(attackType: MeleeAttackType?): Int =
        when (attackType) {
            MeleeAttackType.Stab -> bonuses.defensiveStabBonus(this)
            MeleeAttackType.Slash -> bonuses.defensiveSlashBonus(this)
            MeleeAttackType.Crush -> bonuses.defensiveCrushBonus(this)
            null -> 0
        }
}
