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

    /**
     * Calculates the melee hit chance based on the [player]'s attack roll and the [target]'s
     * defence roll.
     *
     * @param attackType The [MeleeAttackType] used for the attack roll, typically based on the
     *   [player]'s current combat stance.
     * @param attackStyle The [MeleeAttackStyle] used for the attack roll, usually derived from the
     *   [player]'s current stance.
     * @param blockType The [MeleeAttackType] used for the defense roll. In most cases, this matches
     *   [attackType], but certain special attacks may use a different type. For example, the Dragon
     *   Longsword special attack applies the player's current attack type for the attack roll but
     *   always uses `Slash` for the defense roll.
     * @return An integer between `0` and `10,000`, where `0` represents a `0%` hit chance, `1`
     *   represents a `0.01%` hit chance, and `10,000` represents a `100%` hit chance.
     */
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
