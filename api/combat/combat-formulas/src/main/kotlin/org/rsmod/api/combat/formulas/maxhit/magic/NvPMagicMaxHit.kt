package org.rsmod.api.combat.formulas.maxhit.magic

import jakarta.inject.Inject
import org.rsmod.api.combat.formulas.attributes.collector.DamageReductionAttributeCollector
import org.rsmod.api.combat.formulas.maxhit.MaxHitOperations
import org.rsmod.api.combat.maxhit.npc.NpcMagicMaxHit
import org.rsmod.api.npc.magicStrength
import org.rsmod.api.player.bonus.WornBonuses
import org.rsmod.api.random.GameRandom
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player

public class NvPMagicMaxHit
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
        val effectiveMagic = NpcMagicMaxHit.calculateEffectiveMagic(npc.magicLvl)
        val baseDamage = NpcMagicMaxHit.calculateBaseDamage(effectiveMagic, npc.magicStrength)

        val defenceBonus = bonuses.defensiveMagicBonus(target)
        val reductionAttributes = reductions.collect(target, pvp = false, random)
        return MaxHitOperations.applyDamageReductions(baseDamage, defenceBonus, reductionAttributes)
    }
}
