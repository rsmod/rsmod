package org.rsmod.api.combat.formulas

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.styles.MeleeAttackStyle
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.combat.formulas.accuracy.melee.PvNMeleeAccuracy
import org.rsmod.api.random.GameRandom
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player

public class AccuracyFormulae @Inject constructor(private val pvNMeleeAccuracy: PvNMeleeAccuracy) {
    public fun rollMeleeAccuracy(
        player: Player,
        target: Npc,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        blockType: MeleeAttackType?,
        specMultiplier: Double,
        random: GameRandom,
    ): Boolean {
        val hitChance =
            getMeleeHitChance(player, target, attackType, attackStyle, blockType, specMultiplier)
        return pvNMeleeAccuracy.isSuccessfulHit(hitChance, random)
    }

    public fun getMeleeHitChance(
        player: Player,
        target: Npc,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        blockType: MeleeAttackType?,
        specMultiplier: Double = 1.0,
    ): Int =
        pvNMeleeAccuracy.getHitChance(
            player,
            target,
            attackType,
            attackStyle,
            blockType,
            specMultiplier,
        )
}
