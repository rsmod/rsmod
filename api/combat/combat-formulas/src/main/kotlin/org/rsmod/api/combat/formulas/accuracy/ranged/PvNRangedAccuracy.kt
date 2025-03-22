package org.rsmod.api.combat.formulas.accuracy.ranged

import jakarta.inject.Inject
import java.util.EnumSet
import kotlin.math.max
import org.rsmod.api.combat.accuracy.npc.NpcRangedAccuracy
import org.rsmod.api.combat.accuracy.player.PlayerRangedAccuracy
import org.rsmod.api.combat.commons.styles.RangedAttackStyle
import org.rsmod.api.combat.commons.types.RangedAttackType
import org.rsmod.api.combat.formulas.accuracy.AccuracyOperations
import org.rsmod.api.combat.formulas.attributes.CombatNpcAttributes
import org.rsmod.api.combat.formulas.attributes.CombatRangedAttributes
import org.rsmod.api.combat.formulas.attributes.collector.CombatNpcAttributeCollector
import org.rsmod.api.combat.formulas.attributes.collector.CombatRangedAttributeCollector
import org.rsmod.api.combat.formulas.isSlayerTask
import org.rsmod.api.config.refs.params
import org.rsmod.api.player.bonus.WornBonuses
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.type.npc.UnpackedNpcType

public class PvNRangedAccuracy
@Inject
constructor(
    private val bonuses: WornBonuses,
    private val npcAttributes: CombatNpcAttributeCollector,
    private val rangedAttributeCollector: CombatRangedAttributeCollector,
) {
    public fun getHitChance(
        player: Player,
        target: Npc,
        attackType: RangedAttackType?,
        attackStyle: RangedAttackStyle?,
        blockType: RangedAttackType?,
        specialMultiplier: Double,
    ): Int {
        val targetType = target.visType
        val targetMagic = max(target.magicLvl, targetType.param(params.attack_magic))
        val targetDistance = player.coords.chebyshevDistance(target.coords)
        return computeHitChance(
            source = player,
            target = targetType,
            targetDefence = target.defenceLvl,
            targetCurrHp = target.hitpoints,
            targetMaxHp = target.baseHitpointsLvl,
            targetMagic = targetMagic,
            targetDistance = targetDistance,
            attackType = attackType,
            attackStyle = attackStyle,
            blockType = blockType,
            specialMultiplier = specialMultiplier,
        )
    }

    public fun computeHitChance(
        source: Player,
        target: UnpackedNpcType,
        targetDefence: Int,
        targetCurrHp: Int,
        targetMaxHp: Int,
        targetMagic: Int,
        targetDistance: Int,
        attackType: RangedAttackType?,
        attackStyle: RangedAttackStyle?,
        blockType: RangedAttackType?,
        specialMultiplier: Double,
    ): Int {
        val rangeAttributes = rangedAttributeCollector.collect(source, attackType, attackStyle)

        val slayerTask = target.isSlayerTask(source)
        val npcAttributes = npcAttributes.collect(target, targetCurrHp, targetMaxHp, slayerTask)

        val baseAttackRoll =
            computeAttackRoll(
                source = source,
                targetMagic = targetMagic,
                targetDistance = targetDistance,
                attackStyle = attackStyle,
                rangeAttributes = rangeAttributes,
                npcAttributes = npcAttributes,
            )
        val attackRoll = (baseAttackRoll * specialMultiplier).toInt()

        val amascutInvocationLvl = 0 // TODO(combat): Create varp.
        val defenceRoll =
            computeDefenceRoll(
                target = target,
                targetDefence = targetDefence,
                amascutInvocationLvl = amascutInvocationLvl,
                blockType = blockType,
                npcAttributes = npcAttributes,
            )

        return AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
    }

    public fun computeAttackRoll(
        source: Player,
        targetMagic: Int,
        targetDistance: Int,
        attackStyle: RangedAttackStyle?,
        rangeAttributes: EnumSet<CombatRangedAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        val effectiveRanged = RangedAccuracyOperations.calculateEffectiveRanged(source, attackStyle)
        val rangedBonus = bonuses.offensiveRangedBonus(source)
        val attackRoll = PlayerRangedAccuracy.calculateBaseAttackRoll(effectiveRanged, rangedBonus)
        return RangedAccuracyOperations.modifyAttackRoll(
            attackRoll,
            targetMagic,
            targetDistance,
            rangeAttributes,
            npcAttributes,
        )
    }

    public fun computeDefenceRoll(
        target: UnpackedNpcType,
        targetDefence: Int,
        amascutInvocationLvl: Int,
        blockType: RangedAttackType?,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        val effectiveDefence = NpcRangedAccuracy.calculateEffectiveDefence(targetDefence)
        val defenceBonus = target.getDefenceBonus(blockType)
        val defenceRoll = NpcRangedAccuracy.calculateBaseDefenceRoll(effectiveDefence, defenceBonus)
        return AccuracyOperations.modifyDefenceRoll(
            defenceRoll = defenceRoll,
            amascutInvocationLvl = amascutInvocationLvl,
            npcAttributes = npcAttributes,
        )
    }

    private fun UnpackedNpcType.getDefenceBonus(attackType: RangedAttackType?): Int =
        when (attackType) {
            RangedAttackType.Light -> param(params.defence_light)
            RangedAttackType.Standard -> param(params.defence_standard)
            RangedAttackType.Heavy -> param(params.defence_heavy)
            null -> 0
        }
}
