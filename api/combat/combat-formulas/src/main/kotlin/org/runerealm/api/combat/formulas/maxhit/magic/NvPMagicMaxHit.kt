package org.runerealm.api.combat.formulas.maxhit.magic

import jakarta.inject.Inject
import org.runerealm.api.combat.formulas.attributes.collector.DamageReductionAttributeCollector
import org.runerealm.api.combat.formulas.maxhit.MaxHitOperations
import org.runerealm.api.combat.maxhit.npc.NpcMagicMaxHit
import org.runerealm.api.npc.magicStrength
import org.runerealm.api.player.bonus.WornBonuses
import org.runerealm.api.random.GameRandom
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.Player

public class NvPMagicMaxHit
@Inject
constructor(
    private val random: GameRandom,
    private val bonuses: WornBonuses,
    private val reductions: DamageReductionAttributeCollector,
) {
    public fun getMaxHit(npc: Npc, target: Player): Int = computeMaxHit(npc, target)

    public fun computeMaxHit(npc: Npc, target: Player): Int {
        val effectiveMagic = NpcMagicMaxHit.calculateEffectiveMagic(npc.magicLvl)
        val baseDamage = NpcMagicMaxHit.calculateBaseDamage(effectiveMagic, npc.magicStrength)

        val defenceBonus = bonuses.defensiveMagicBonus(target)
        val reductionAttributes = reductions.collectNvP(target, random)
        return MaxHitOperations.applyDamageReductions(baseDamage, defenceBonus, reductionAttributes)
    }
}
