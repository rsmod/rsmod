package org.rsmod.api.combat.formulas.accuracy.magic

import jakarta.inject.Inject
import java.util.EnumSet
import org.rsmod.api.combat.accuracy.npc.NpcMagicAccuracy
import org.rsmod.api.combat.accuracy.player.PlayerMagicAccuracy
import org.rsmod.api.combat.commons.magic.Spellbook
import org.rsmod.api.combat.commons.styles.MagicAttackStyle
import org.rsmod.api.combat.formulas.accuracy.AccuracyOperations
import org.rsmod.api.combat.formulas.attributes.CombatNpcAttributes
import org.rsmod.api.combat.formulas.attributes.CombatSpellAttributes
import org.rsmod.api.combat.formulas.attributes.CombatStaffAttributes
import org.rsmod.api.combat.formulas.attributes.collector.CombatMagicAttributeCollector
import org.rsmod.api.combat.formulas.attributes.collector.CombatNpcAttributeCollector
import org.rsmod.api.combat.formulas.isSlayerTask
import org.rsmod.api.combat.formulas.scale
import org.rsmod.api.config.refs.params
import org.rsmod.api.player.bonus.WornBonuses
import org.rsmod.api.random.GameRandom
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.game.type.obj.ObjType

public class PvNMagicAccuracy
@Inject
constructor(
    private val random: GameRandom,
    private val bonuses: WornBonuses,
    private val magicAttributes: CombatMagicAttributeCollector,
    private val npcAttributes: CombatNpcAttributeCollector,
) {
    public fun getSpellHitChance(
        player: Player,
        target: Npc,
        spell: ObjType,
        spellbook: Spellbook?,
        usedSunfireRune: Boolean,
    ): Int {
        val targetType = target.visType
        val elementalWeakness = targetType.param(params.elemental_weakness_percent)
        return computeSpellHitChance(
            source = player,
            target = targetType,
            spell = spell,
            targetDefence = target.defenceLvl,
            targetCurrHp = target.hitpoints,
            targetMaxHp = target.baseHitpointsLvl,
            targetMagic = target.magicLvl,
            targetWeaknessPercent = elementalWeakness,
            spellbook = spellbook,
            usedSunfireRune = usedSunfireRune,
        )
    }

    public fun computeSpellHitChance(
        source: Player,
        target: UnpackedNpcType,
        spell: ObjType,
        targetDefence: Int,
        targetCurrHp: Int,
        targetMaxHp: Int,
        targetMagic: Int,
        targetWeaknessPercent: Int,
        spellbook: Spellbook?,
        usedSunfireRune: Boolean,
    ): Int {
        val spellAttributes =
            magicAttributes.spellCollect(source, spell, spellbook, usedSunfireRune, random)

        val slayerTask = target.isSlayerTask(source)
        val npcAttributes = npcAttributes.collect(target, targetCurrHp, targetMaxHp, slayerTask)

        val attackRoll =
            computeSpellAttackRoll(
                source = source,
                targetWeaknessPercent = targetWeaknessPercent,
                spellAttributes = spellAttributes,
                npcAttributes = npcAttributes,
            )

        val amascutInvocationLvl = 0 // TODO(combat): Create varp.
        val baseDefenceRoll =
            computeDefenceRoll(
                target = target,
                targetDefence = targetDefence,
                targetMagic = targetMagic,
                amascutInvocationLvl = amascutInvocationLvl,
                npcAttributes = npcAttributes,
            )
        val defenceRoll = modifySpellDefenceRoll(baseDefenceRoll, spellAttributes)

        return AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
    }

    public fun computeSpellAttackRoll(
        source: Player,
        targetWeaknessPercent: Int,
        spellAttributes: EnumSet<CombatSpellAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        val effectiveMagic = MagicAccuracyOperations.calculateEffectiveMagic(source, null)
        val magicBonus = bonuses.offensiveMagicBonus(source)
        val attackRoll = PlayerMagicAccuracy.calculateBaseAttackRoll(effectiveMagic, magicBonus)
        return MagicAccuracyOperations.modifySpellAttackRoll(
            attackRoll = attackRoll,
            targetWeaknessPercent = targetWeaknessPercent,
            spellAttributes = spellAttributes,
            npcAttributes = npcAttributes,
        )
    }

    public fun getStaffHitChance(player: Player, target: Npc, attackStyle: MagicAttackStyle?): Int =
        computeStaffHitChance(
            source = player,
            target = target.visType,
            targetDefence = target.defenceLvl,
            targetCurrHp = target.hitpoints,
            targetMaxHp = target.baseHitpointsLvl,
            targetMagic = target.magicLvl,
            attackStyle = attackStyle,
        )

    public fun computeStaffHitChance(
        source: Player,
        target: UnpackedNpcType,
        targetDefence: Int,
        targetCurrHp: Int,
        targetMaxHp: Int,
        targetMagic: Int,
        attackStyle: MagicAttackStyle?,
    ): Int {
        val staffAttributes = magicAttributes.staffCollect(source, random)

        val slayerTask = target.isSlayerTask(source)
        val npcAttributes = npcAttributes.collect(target, targetCurrHp, targetMaxHp, slayerTask)

        val attackRoll = computeStaffAttackRoll(source, attackStyle, staffAttributes, npcAttributes)

        val amascutInvocationLvl = 0 // TODO(combat): Create varp.
        val baseDefenceRoll =
            computeDefenceRoll(
                target = target,
                targetDefence = targetDefence,
                targetMagic = targetMagic,
                amascutInvocationLvl = amascutInvocationLvl,
                npcAttributes = npcAttributes,
            )
        val defenceRoll = modifyStaffDefenceRoll(baseDefenceRoll, staffAttributes)

        return AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
    }

    public fun computeStaffAttackRoll(
        source: Player,
        attackStyle: MagicAttackStyle?,
        staffAttributes: EnumSet<CombatStaffAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        val effectiveMagic = MagicAccuracyOperations.calculateEffectiveMagic(source, attackStyle)
        val magicBonus = bonuses.offensiveMagicBonus(source)
        val attackRoll = PlayerMagicAccuracy.calculateBaseAttackRoll(effectiveMagic, magicBonus)
        return MagicAccuracyOperations.modifyStaffAttackRoll(
            attackRoll = attackRoll,
            staffAttributes = staffAttributes,
            npcAttributes = npcAttributes,
        )
    }

    public fun computeDefenceRoll(
        target: UnpackedNpcType,
        targetDefence: Int,
        targetMagic: Int,
        amascutInvocationLvl: Int,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        val defenceLevel =
            if (target.param(params.magic_defence_uses_defence_level)) {
                targetDefence
            } else {
                targetMagic
            }
        val effectiveDefence = NpcMagicAccuracy.calculateEffectiveDefence(defenceLevel)
        val defenceBonus = target.param(params.defence_magic)
        val defenceRoll = NpcMagicAccuracy.calculateBaseDefenceRoll(effectiveDefence, defenceBonus)
        return AccuracyOperations.modifyDefenceRoll(
            defenceRoll = defenceRoll,
            amascutInvocationLvl = amascutInvocationLvl,
            npcAttributes = npcAttributes,
        )
    }

    private fun modifyDefenceRoll(defenceRoll: Int, brimstonePassive: Boolean): Int {
        var modified = defenceRoll

        if (brimstonePassive) {
            modified = scale(modified, multiplier = 9, divisor = 10)
        }

        return modified
    }

    private fun modifySpellDefenceRoll(
        defenceRoll: Int,
        spellAttributes: EnumSet<CombatSpellAttributes>,
    ): Int {
        val brimstonePassive = CombatSpellAttributes.BrimstonePassive in spellAttributes
        return modifyDefenceRoll(defenceRoll, brimstonePassive)
    }

    private fun modifyStaffDefenceRoll(
        defenceRoll: Int,
        spellAttributes: EnumSet<CombatStaffAttributes>,
    ): Int {
        val brimstonePassive = CombatStaffAttributes.BrimstonePassive in spellAttributes
        return modifyDefenceRoll(defenceRoll, brimstonePassive)
    }
}
