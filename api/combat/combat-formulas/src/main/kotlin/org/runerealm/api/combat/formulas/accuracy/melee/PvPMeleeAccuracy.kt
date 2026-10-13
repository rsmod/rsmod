package org.runerealm.api.combat.formulas.accuracy.melee

import jakarta.inject.Inject
import java.util.EnumSet
import org.runerealm.api.combat.accuracy.player.PlayerMeleeAccuracy
import org.runerealm.api.combat.commons.styles.MeleeAttackStyle
import org.runerealm.api.combat.commons.types.MeleeAttackType
import org.runerealm.api.combat.formulas.accuracy.AccuracyOperations
import org.runerealm.api.combat.formulas.attributes.CombatMeleeAttributes
import org.runerealm.api.combat.formulas.attributes.CombatNpcAttributes
import org.runerealm.api.combat.formulas.attributes.collector.CombatMeleeAttributeCollector
import org.runerealm.api.combat.weapon.styles.AttackStyles
import org.runerealm.api.player.bonus.WornBonuses
import org.runerealm.game.entity.Player

public class PvPMeleeAccuracy
@Inject
constructor(
    private val bonuses: WornBonuses,
    private val attackStyles: AttackStyles,
    private val meleeAttributes: CombatMeleeAttributeCollector,
) {
    public fun getHitChance(
        player: Player,
        target: Player,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        blockType: MeleeAttackType?,
        specialMultiplier: Double,
    ): Int =
        computeHitChance(
            source = player,
            target = target,
            attackType = attackType,
            attackStyle = attackStyle,
            blockType = blockType,
            specialMultiplier = specialMultiplier,
        )

    public fun computeHitChance(
        source: Player,
        target: Player,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        blockType: MeleeAttackType?,
        specialMultiplier: Double,
    ): Int {
        val npcAttributes = EnumSet.noneOf(CombatNpcAttributes::class.java)
        val meleeAttributes = meleeAttributes.collect(source, attackType)

        val baseAttackRoll =
            computeAttackRoll(source, attackType, attackStyle, meleeAttributes, npcAttributes)
        val attackRoll = (baseAttackRoll * specialMultiplier).toInt()

        val defenceRoll = computeDefenceRoll(target, blockType)

        val hitChance = AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
        return MeleeAccuracyOperations.modifyHitChance(
            hitChance = hitChance,
            attackRoll = attackRoll,
            defenceRoll = defenceRoll,
            meleeAttributes = meleeAttributes,
            npcAttributes = npcAttributes,
        )
    }

    public fun computeAttackRoll(
        source: Player,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        meleeAttributes: EnumSet<CombatMeleeAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        val effectiveAttack = MeleeAccuracyOperations.calculateEffectiveAttack(source, attackStyle)
        val attackBonus = source.getAttackBonus(attackType)
        val attackRoll = PlayerMeleeAccuracy.calculateBaseAttackRoll(effectiveAttack, attackBonus)
        return MeleeAccuracyOperations.modifyAttackRoll(attackRoll, meleeAttributes, npcAttributes)
    }

    private fun Player.getAttackBonus(attackType: MeleeAttackType?): Int =
        when (attackType) {
            MeleeAttackType.Stab -> bonuses.offensiveStabBonus(this)
            MeleeAttackType.Slash -> bonuses.offensiveSlashBonus(this)
            MeleeAttackType.Crush -> bonuses.offensiveCrushBonus(this)
            null -> 0
        }

    public fun computeDefenceRoll(target: Player, attackType: MeleeAttackType?): Int {
        val targetAttackStyle = attackStyles.get(target)
        val effectiveDefence =
            MeleeAccuracyOperations.calculateEffectiveDefence(target, targetAttackStyle)
        val defenceBonus = target.getDefenceBonus(attackType)
        return PlayerMeleeAccuracy.calculateBaseDefenceRoll(effectiveDefence, defenceBonus)
    }

    private fun Player.getDefenceBonus(attackType: MeleeAttackType?): Int =
        when (attackType) {
            MeleeAttackType.Stab -> bonuses.defensiveStabBonus(this)
            MeleeAttackType.Slash -> bonuses.defensiveSlashBonus(this)
            MeleeAttackType.Crush -> bonuses.defensiveCrushBonus(this)
            null -> 0
        }
}
