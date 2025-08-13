package org.rsmod.api.combat.formulas.accuracy.multi

import jakarta.inject.Inject
import java.util.EnumSet
import kotlin.math.max
import org.rsmod.api.combat.commons.styles.MagicAttackStyle
import org.rsmod.api.combat.commons.styles.MeleeAttackStyle
import org.rsmod.api.combat.commons.styles.RangedAttackStyle
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.combat.commons.types.RangedAttackType
import org.rsmod.api.combat.formulas.accuracy.AccuracyOperations
import org.rsmod.api.combat.formulas.accuracy.magic.PvNMagicAccuracy
import org.rsmod.api.combat.formulas.accuracy.melee.PvNMeleeAccuracy
import org.rsmod.api.combat.formulas.accuracy.ranged.PvNRangedAccuracy
import org.rsmod.api.combat.formulas.attributes.CombatStaffAttributes
import org.rsmod.api.combat.formulas.attributes.collector.CombatMeleeAttributeCollector
import org.rsmod.api.combat.formulas.attributes.collector.CombatNpcAttributeCollector
import org.rsmod.api.combat.formulas.attributes.collector.CombatRangedAttributeCollector
import org.rsmod.api.combat.formulas.isSlayerTask
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.varbits
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.type.npc.UnpackedNpcType

public class PvNMultiStyleAccuracy
@Inject
constructor(
    private val magic: PvNMagicAccuracy,
    private val melee: PvNMeleeAccuracy,
    private val ranged: PvNRangedAccuracy,
    private val npcAttributes: CombatNpcAttributeCollector,
    private val meleeAttributes: CombatMeleeAttributeCollector,
    private val rangedAttributes: CombatRangedAttributeCollector,
) {
    public fun getMagicalMeleeHitChance(
        player: Player,
        target: Npc,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        specialMultiplier: Double,
    ): Int {
        return computeMagicalMeleeHitChance(
            source = player,
            target = target.visType,
            targetDefence = target.defenceLvl,
            targetCurrHp = target.hitpoints,
            targetMaxHp = target.baseHitpointsLvl,
            targetMagic = target.magicLvl,
            attackType = attackType,
            attackStyle = attackStyle,
            specialMultiplier = specialMultiplier,
        )
    }

    private fun computeMagicalMeleeHitChance(
        source: Player,
        target: UnpackedNpcType,
        targetDefence: Int,
        targetCurrHp: Int,
        targetMaxHp: Int,
        targetMagic: Int,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        specialMultiplier: Double,
    ): Int {
        val meleeAttributes = meleeAttributes.collect(source, attackType)

        val slayerTask = target.isSlayerTask(source)
        val npcAttributes = npcAttributes.collect(target, targetCurrHp, targetMaxHp, slayerTask)

        val baseAttackRoll =
            melee.computeAttackRoll(source, attackType, attackStyle, meleeAttributes, npcAttributes)
        val attackRoll = (baseAttackRoll * specialMultiplier).toInt()

        val amascutInvocationLvl = source.vars[varbits.toa_client_raid_level]
        val defenceRoll =
            magic.computeDefenceRoll(
                target = target,
                targetDefence = targetDefence,
                targetMagic = targetMagic,
                amascutInvocationLvl = amascutInvocationLvl,
                npcAttributes = npcAttributes,
            )

        return AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
    }

    public fun getRangedMeleeHitChance(
        player: Player,
        target: Npc,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        blockType: RangedAttackType?,
        specialMultiplier: Double,
    ): Int {
        return computeRangedMeleeHitChance(
            source = player,
            target = target.visType,
            targetDefence = target.defenceLvl,
            targetCurrHp = target.hitpoints,
            targetMaxHp = target.baseHitpointsLvl,
            attackType = attackType,
            attackStyle = attackStyle,
            blockType = blockType,
            specialMultiplier = specialMultiplier,
        )
    }

    private fun computeRangedMeleeHitChance(
        source: Player,
        target: UnpackedNpcType,
        targetDefence: Int,
        targetCurrHp: Int,
        targetMaxHp: Int,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        blockType: RangedAttackType?,
        specialMultiplier: Double,
    ): Int {
        val meleeAttributes = meleeAttributes.collect(source, attackType)

        val slayerTask = target.isSlayerTask(source)
        val npcAttributes = npcAttributes.collect(target, targetCurrHp, targetMaxHp, slayerTask)

        val baseAttackRoll =
            melee.computeAttackRoll(source, attackType, attackStyle, meleeAttributes, npcAttributes)
        val attackRoll = (baseAttackRoll * specialMultiplier).toInt()

        val amascutInvocationLvl = source.vars[varbits.toa_client_raid_level]
        val defenceRoll =
            ranged.computeDefenceRoll(
                target = target,
                targetDefence = targetDefence,
                amascutInvocationLvl = amascutInvocationLvl,
                blockType = blockType,
                npcAttributes = npcAttributes,
            )

        return AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
    }

    public fun getRangedMagicHitChance(
        player: Player,
        target: Npc,
        attackStyle: MagicAttackStyle?,
        blockType: RangedAttackType?,
        specialMultiplier: Double,
    ): Int {
        return computeRangedMagicHitChance(
            source = player,
            target = target.visType,
            targetDefence = target.defenceLvl,
            targetCurrHp = target.hitpoints,
            targetMaxHp = target.baseHitpointsLvl,
            attackStyle = attackStyle,
            blockType = blockType,
            specialMultiplier = specialMultiplier,
        )
    }

    private fun computeRangedMagicHitChance(
        source: Player,
        target: UnpackedNpcType,
        targetDefence: Int,
        targetCurrHp: Int,
        targetMaxHp: Int,
        attackStyle: MagicAttackStyle?,
        blockType: RangedAttackType?,
        specialMultiplier: Double,
    ): Int {
        val slayerTask = target.isSlayerTask(source)
        val npcAttributes = npcAttributes.collect(target, targetCurrHp, targetMaxHp, slayerTask)

        // TODO(combat): Should we use `computeSpellAttackRoll` instead to take elemental weakness
        // into account?
        val baseAttackRoll =
            magic.computeStaffAttackRoll(source, attackStyle, EMPTY_STAFF_ATTRIBUTES, npcAttributes)
        val attackRoll = (baseAttackRoll * specialMultiplier).toInt()

        val amascutInvocationLvl = source.vars[varbits.toa_client_raid_level]
        val defenceRoll =
            ranged.computeDefenceRoll(
                target = target,
                targetDefence = targetDefence,
                amascutInvocationLvl = amascutInvocationLvl,
                blockType = blockType,
                npcAttributes = npcAttributes,
            )

        return AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
    }

    public fun getMagicalRangedHitChance(
        player: Player,
        target: Npc,
        attackType: RangedAttackType?,
        attackStyle: RangedAttackStyle?,
        specialMultiplier: Double,
    ): Int {
        val targetType = target.visType
        val targetMagic = max(target.magicLvl, targetType.param(params.attack_magic))
        val targetDistance = player.coords.chebyshevDistance(target.coords)
        return computeMagicalRangedHitChance(
            source = player,
            target = targetType,
            targetDefence = target.defenceLvl,
            targetCurrHp = target.hitpoints,
            targetMaxHp = target.baseHitpointsLvl,
            targetMagic = targetMagic,
            targetDistance = targetDistance,
            attackType = attackType,
            attackStyle = attackStyle,
            specialMultiplier = specialMultiplier,
        )
    }

    private fun computeMagicalRangedHitChance(
        source: Player,
        target: UnpackedNpcType,
        targetDefence: Int,
        targetCurrHp: Int,
        targetMaxHp: Int,
        targetMagic: Int,
        targetDistance: Int,
        attackType: RangedAttackType?,
        attackStyle: RangedAttackStyle?,
        specialMultiplier: Double,
    ): Int {
        val rangeAttributes = rangedAttributes.collect(source, attackType, attackStyle)

        val slayerTask = target.isSlayerTask(source)
        val npcAttributes = npcAttributes.collect(target, targetCurrHp, targetMaxHp, slayerTask)

        val baseAttackRoll =
            ranged.computeAttackRoll(
                source = source,
                targetMagic = targetMagic,
                targetDistance = targetDistance,
                attackStyle = attackStyle,
                rangeAttributes = rangeAttributes,
                npcAttributes = npcAttributes,
            )
        val attackRoll = (baseAttackRoll * specialMultiplier).toInt()

        val amascutInvocationLvl = source.vars[varbits.toa_client_raid_level]
        val defenceRoll =
            magic.computeDefenceRoll(
                target = target,
                targetDefence = targetDefence,
                targetMagic = targetMagic,
                amascutInvocationLvl = amascutInvocationLvl,
                npcAttributes = npcAttributes,
            )

        return AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
    }

    private companion object {
        private val EMPTY_STAFF_ATTRIBUTES = EnumSet.noneOf(CombatStaffAttributes::class.java)
    }
}
