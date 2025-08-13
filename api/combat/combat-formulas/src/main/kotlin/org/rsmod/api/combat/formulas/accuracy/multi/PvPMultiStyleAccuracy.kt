package org.rsmod.api.combat.formulas.accuracy.multi

import jakarta.inject.Inject
import java.util.EnumSet
import org.rsmod.api.combat.commons.styles.MagicAttackStyle
import org.rsmod.api.combat.commons.styles.MeleeAttackStyle
import org.rsmod.api.combat.commons.styles.RangedAttackStyle
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.combat.commons.types.RangedAttackType
import org.rsmod.api.combat.formulas.accuracy.AccuracyOperations
import org.rsmod.api.combat.formulas.accuracy.magic.PvPMagicAccuracy
import org.rsmod.api.combat.formulas.accuracy.melee.PvPMeleeAccuracy
import org.rsmod.api.combat.formulas.accuracy.ranged.PvPRangedAccuracy
import org.rsmod.api.combat.formulas.attributes.CombatNpcAttributes
import org.rsmod.api.combat.formulas.attributes.collector.CombatMeleeAttributeCollector
import org.rsmod.api.combat.formulas.attributes.collector.CombatRangedAttributeCollector
import org.rsmod.game.entity.Player

public class PvPMultiStyleAccuracy
@Inject
constructor(
    private val magic: PvPMagicAccuracy,
    private val melee: PvPMeleeAccuracy,
    private val ranged: PvPRangedAccuracy,
    private val meleeAttributes: CombatMeleeAttributeCollector,
    private val rangedAttributes: CombatRangedAttributeCollector,
) {
    public fun getMagicalMeleeHitChance(
        player: Player,
        target: Player,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        specialMultiplier: Double,
    ): Int {
        return computeMagicalMeleeHitChance(
            source = player,
            target = target,
            attackType = attackType,
            attackStyle = attackStyle,
            specialMultiplier = specialMultiplier,
        )
    }

    private fun computeMagicalMeleeHitChance(
        source: Player,
        target: Player,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        specialMultiplier: Double,
    ): Int {
        val npcAttributes = EnumSet.noneOf(CombatNpcAttributes::class.java)
        val meleeAttributes = meleeAttributes.collect(source, attackType)

        val baseAttackRoll =
            melee.computeAttackRoll(source, attackType, attackStyle, meleeAttributes, npcAttributes)
        val attackRoll = (baseAttackRoll * specialMultiplier).toInt()

        val defenceRoll = magic.computeDefenceRoll(target)

        return AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
    }

    public fun getRangedMeleeHitChance(
        player: Player,
        target: Player,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        specialMultiplier: Double,
    ): Int {
        return computeRangedMeleeHitChance(
            source = player,
            target = target,
            attackType = attackType,
            attackStyle = attackStyle,
            specialMultiplier = specialMultiplier,
        )
    }

    private fun computeRangedMeleeHitChance(
        source: Player,
        target: Player,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        specialMultiplier: Double,
    ): Int {
        val npcAttributes = EnumSet.noneOf(CombatNpcAttributes::class.java)
        val meleeAttributes = meleeAttributes.collect(source, attackType)

        val baseAttackRoll =
            melee.computeAttackRoll(source, attackType, attackStyle, meleeAttributes, npcAttributes)
        val attackRoll = (baseAttackRoll * specialMultiplier).toInt()

        val defenceRoll = ranged.computeDefenceRoll(target)

        return AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
    }

    public fun getRangedMagicHitChance(
        player: Player,
        target: Player,
        attackStyle: MagicAttackStyle?,
        specialMultiplier: Double,
    ): Int {
        return computeRangedMagicHitChance(
            player = player,
            target = target,
            attackStyle = attackStyle,
            specialMultiplier = specialMultiplier,
        )
    }

    private fun computeRangedMagicHitChance(
        player: Player,
        target: Player,
        attackStyle: MagicAttackStyle?,
        specialMultiplier: Double,
    ): Int {
        val baseAttackRoll = magic.computeStaffAttackRoll(player, attackStyle)
        val attackRoll = (baseAttackRoll * specialMultiplier).toInt()
        val defenceRoll = ranged.computeDefenceRoll(target)
        return AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
    }

    public fun getMagicalRangedHitChance(
        player: Player,
        target: Player,
        attackType: RangedAttackType?,
        attackStyle: RangedAttackStyle?,
        specialMultiplier: Double,
    ): Int {
        val targetDistance = player.coords.chebyshevDistance(target.coords)
        return computeMagicalRangedHitChance(
            source = player,
            target = target,
            targetDistance = targetDistance,
            attackType = attackType,
            attackStyle = attackStyle,
            specialMultiplier = specialMultiplier,
        )
    }

    private fun computeMagicalRangedHitChance(
        source: Player,
        target: Player,
        targetDistance: Int,
        attackType: RangedAttackType?,
        attackStyle: RangedAttackStyle?,
        specialMultiplier: Double,
    ): Int {
        val rangeAttributes = rangedAttributes.collect(source, attackType, attackStyle)

        val baseAttackRoll =
            ranged.computeAttackRoll(source, targetDistance, attackStyle, rangeAttributes)
        val attackRoll = (baseAttackRoll * specialMultiplier).toInt()

        val defenceRoll = magic.computeDefenceRoll(target)
        return AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
    }
}
