package org.runerealm.api.combat.formulas.accuracy.ranged

import jakarta.inject.Inject
import java.util.EnumSet
import org.runerealm.api.combat.accuracy.player.PlayerRangedAccuracy
import org.runerealm.api.combat.commons.styles.RangedAttackStyle
import org.runerealm.api.combat.commons.types.RangedAttackType
import org.runerealm.api.combat.formulas.accuracy.AccuracyOperations
import org.runerealm.api.combat.formulas.attributes.CombatRangedAttributes
import org.runerealm.api.combat.formulas.attributes.collector.CombatRangedAttributeCollector
import org.runerealm.api.combat.weapon.styles.AttackStyles
import org.runerealm.api.player.bonus.WornBonuses
import org.runerealm.game.entity.Player

public class PvPRangedAccuracy
@Inject
constructor(
    private val bonuses: WornBonuses,
    private val attackStyles: AttackStyles,
    private val rangedAttributes: CombatRangedAttributeCollector,
) {
    public fun getHitChance(
        player: Player,
        target: Player,
        attackType: RangedAttackType?,
        attackStyle: RangedAttackStyle?,
        specialMultiplier: Double,
    ): Int {
        val targetDistance = player.coords.chebyshevDistance(target.coords)
        return computeHitChance(
            source = player,
            target = target,
            targetDistance = targetDistance,
            attackType = attackType,
            attackStyle = attackStyle,
            specialMultiplier = specialMultiplier,
        )
    }

    public fun computeHitChance(
        source: Player,
        target: Player,
        targetDistance: Int,
        attackType: RangedAttackType?,
        attackStyle: RangedAttackStyle?,
        specialMultiplier: Double,
    ): Int {
        val rangeAttributes = rangedAttributes.collect(source, attackType, attackStyle)

        val baseAttackRoll = computeAttackRoll(source, targetDistance, attackStyle, rangeAttributes)
        val attackRoll = (baseAttackRoll * specialMultiplier).toInt()

        val defenceRoll = computeDefenceRoll(target)
        return AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
    }

    public fun computeAttackRoll(
        source: Player,
        targetDistance: Int,
        attackStyle: RangedAttackStyle?,
        rangeAttributes: EnumSet<CombatRangedAttributes>,
    ): Int {
        val effectiveRanged = RangedAccuracyOperations.calculateEffectiveRanged(source, attackStyle)
        val rangedBonus = bonuses.offensiveRangedBonus(source)
        val attackRoll = PlayerRangedAccuracy.calculateBaseAttackRoll(effectiveRanged, rangedBonus)
        return RangedAccuracyOperations.modifyAttackRoll(
            attackRoll = attackRoll,
            targetDistance = targetDistance,
            rangeAttributes = rangeAttributes,
        )
    }

    public fun computeDefenceRoll(target: Player): Int {
        val targetAttackStyle = attackStyles.get(target)
        val effectiveDefence =
            RangedAccuracyOperations.calculateEffectiveDefence(target, targetAttackStyle)
        val defenceBonus = bonuses.defensiveRangedBonus(target)
        return PlayerRangedAccuracy.calculateBaseDefenceRoll(effectiveDefence, defenceBonus)
    }
}
