package org.rsmod.api.combat.formulas.accuracy.magic

import jakarta.inject.Inject
import java.util.EnumSet
import org.rsmod.api.combat.accuracy.player.PlayerMagicAccuracy
import org.rsmod.api.combat.commons.magic.Spellbook
import org.rsmod.api.combat.commons.styles.MagicAttackStyle
import org.rsmod.api.combat.formulas.accuracy.AccuracyOperations
import org.rsmod.api.combat.formulas.attributes.CombatSpellAttributes
import org.rsmod.api.combat.formulas.attributes.collector.CombatMagicAttributeCollector
import org.rsmod.api.combat.weapon.styles.AttackStyles
import org.rsmod.api.player.bonus.WornBonuses
import org.rsmod.api.random.GameRandom
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjType

public class PvPMagicAccuracy
@Inject
constructor(
    private val random: GameRandom,
    private val bonuses: WornBonuses,
    private val attackStyles: AttackStyles,
    private val magicAttributes: CombatMagicAttributeCollector,
) {
    public fun getSpellHitChance(
        player: Player,
        target: Player,
        spell: ObjType,
        spellbook: Spellbook?,
        usedSunfireRune: Boolean,
    ): Int =
        computeSpellHitChance(
            source = player,
            target = target,
            spell = spell,
            spellbook = spellbook,
            usedSunfireRune = usedSunfireRune,
        )

    public fun computeSpellHitChance(
        source: Player,
        target: Player,
        spell: ObjType,
        spellbook: Spellbook?,
        usedSunfireRune: Boolean,
    ): Int {
        val spellAttributes =
            magicAttributes.spellCollect(source, spell, spellbook, usedSunfireRune, random)
        val attackRoll = computeSpellAttackRoll(source, spellAttributes)
        val defenceRoll = computeDefenceRoll(target)
        return AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
    }

    public fun computeSpellAttackRoll(
        source: Player,
        spellAttributes: EnumSet<CombatSpellAttributes>,
    ): Int {
        val effectiveMagic = MagicAccuracyOperations.calculateEffectiveMagic(source, null)
        val magicBonus = bonuses.offensiveMagicBonus(source)
        val attackRoll = PlayerMagicAccuracy.calculateBaseAttackRoll(effectiveMagic, magicBonus)
        return MagicAccuracyOperations.modifySpellAttackRoll(attackRoll, spellAttributes)
    }

    public fun getStaffHitChance(
        player: Player,
        target: Player,
        attackStyle: MagicAttackStyle?,
        specialMultiplier: Double,
    ): Int = computeStaffHitChance(player, target, attackStyle, specialMultiplier)

    public fun computeStaffHitChance(
        source: Player,
        target: Player,
        attackStyle: MagicAttackStyle?,
        specialMultiplier: Double,
    ): Int {
        val baseAttackRoll = computeStaffAttackRoll(source, attackStyle)
        val attackRoll = (baseAttackRoll * specialMultiplier).toInt()
        val defenceRoll = computeDefenceRoll(target)
        return AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
    }

    public fun computeStaffAttackRoll(source: Player, attackStyle: MagicAttackStyle?): Int {
        val effectiveMagic = MagicAccuracyOperations.calculateEffectiveMagic(source, attackStyle)
        val magicBonus = bonuses.offensiveMagicBonus(source)
        val attackRoll = PlayerMagicAccuracy.calculateBaseAttackRoll(effectiveMagic, magicBonus)
        return MagicAccuracyOperations.modifyStaffAttackRoll(attackRoll)
    }

    public fun computeDefenceRoll(target: Player): Int {
        val targetAttackStyle = attackStyles.get(target)
        val effectiveDefence =
            MagicAccuracyOperations.calculateEffectiveDefence(target, targetAttackStyle)
        val defenceBonus = bonuses.defensiveMagicBonus(target)
        return PlayerMagicAccuracy.calculateBaseDefenceRoll(effectiveDefence, defenceBonus)
    }
}
