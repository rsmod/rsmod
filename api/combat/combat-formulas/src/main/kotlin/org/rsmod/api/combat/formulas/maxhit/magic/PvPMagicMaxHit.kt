package org.rsmod.api.combat.formulas.maxhit.magic

import jakarta.inject.Inject
import java.util.EnumSet
import org.rsmod.api.combat.commons.magic.Spellbook
import org.rsmod.api.combat.formulas.attributes.CombatSpellAttributes
import org.rsmod.api.combat.formulas.attributes.collector.CombatMagicAttributeCollector
import org.rsmod.api.combat.formulas.attributes.collector.DamageReductionAttributeCollector
import org.rsmod.api.combat.formulas.maxhit.MaxHitOperations
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.bonus.WornBonuses
import org.rsmod.api.player.stat.magicLvl
import org.rsmod.api.player.vars.intVarp
import org.rsmod.api.random.GameRandom
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjType

public class PvPMagicMaxHit
@Inject
constructor(
    private val random: GameRandom,
    private val bonuses: WornBonuses,
    private val magicAttributes: CombatMagicAttributeCollector,
    private val reductions: DamageReductionAttributeCollector,
) {
    private var Player.maxHit by intVarp(varps.com_maxhit)

    /**
     * Computes the maximum magic hit for a normal spell cast by [player] against [target].
     *
     * **Notes:**
     * - This function should be used instead of [computeSpellMaxHit] in most cases to ensure
     *   consistency in spell hit calculations. Future optimizations may depend on this function as
     *   the main entry point.
     * - The `com_maxhit` varp for [player] is updated with the computed max hit.
     *
     * @param spell The [ObjType] representing the spell being cast (e.g., `objs.spell_wind_strike`
     *   for the Wind Strike spell).
     * @param spellbook The [Spellbook] the spell belongs to (e.g., Standard or Ancients), usually
     *   derived from the player's current spellbook.
     * @param baseMaxHit The spell's base max hit, used as a baseline for calculating the maximum
     *   (and minimum, when applicable) hit.
     * @param usedSunfireRune Whether the player used a Sunfire rune for this cast.
     */
    public fun getSpellMaxHit(
        player: Player,
        target: Player,
        spell: ObjType,
        spellbook: Spellbook?,
        baseMaxHit: Int,
        usedSunfireRune: Boolean,
    ): IntRange {
        val maxHit =
            computeSpellMaxHit(
                source = player,
                target = target,
                spell = spell,
                baseMaxHit = baseMaxHit,
                spellbook = spellbook,
                usedSunfireRune = usedSunfireRune,
            )
        player.maxHit = maxHit.last
        return maxHit
    }

    public fun computeSpellMaxHit(
        source: Player,
        target: Player,
        spell: ObjType,
        baseMaxHit: Int,
        spellbook: Spellbook?,
        usedSunfireRune: Boolean,
    ): IntRange {
        val spellAttributes =
            magicAttributes.spellCollect(source, spell, spellbook, usedSunfireRune, random)

        val modifiedDamage = computeSpellModifiedDamage(source, baseMaxHit, spellAttributes)
        val modifiedDamageRange =
            MagicMaxHitOperations.modifySpellDamageRange(modifiedDamage, spellAttributes)

        val defenceBonus = bonuses.defensiveMagicBonus(target)
        val reductionAttributes = reductions.collectPvP(target, random)

        // We could add a fast-path here and return early if `reductionAttributes` is empty.
        // However, avoiding branching can sometimes be preferable, as it may prevent subtle,
        // hard-to-detect bugs. Since `applyDamageReductions` is (currently) cheap, this is
        // one of those cases.

        val reducedMinHit =
            MaxHitOperations.applyDamageReductions(
                startDamage = modifiedDamageRange.first,
                activeDefenceBonus = defenceBonus,
                reductionAttributes = reductionAttributes,
            )

        val reducedMaxHit =
            MaxHitOperations.applyDamageReductions(
                startDamage = modifiedDamageRange.last,
                activeDefenceBonus = defenceBonus,
                reductionAttributes = reductionAttributes,
            )

        return reducedMinHit..reducedMaxHit
    }

    public fun computeSpellModifiedDamage(
        source: Player,
        baseDamage: Int,
        spellAttributes: EnumSet<CombatSpellAttributes>,
    ): Int {
        val magicDmgBonus = bonuses.magicDamageBonusBase(source)
        val prayerDmgBonus = MagicMaxHitOperations.getMagicDamagePrayerBonus(source)
        return MagicMaxHitOperations.modifySpellBaseDamage(
            baseDamage = baseDamage,
            sourceMagic = source.magicLvl,
            sourceBaseMagicDmgBonus = magicDmgBonus,
            sourceMagicPrayerBonus = prayerDmgBonus,
            spellAttributes = spellAttributes,
        )
    }

    /**
     * Computes the maximum magic hit for a powered staff's built-in spell used by [player] against
     * [target].
     *
     * **Notes:**
     * - This function should be used instead of [computeStaffMaxHit] in most cases to ensure
     *   consistency in staff hit calculations. Future optimizations may depend on this function as
     *   the main entry point.
     * - The `com_maxhit` varp for [player] is updated with the computed max hit.
     *
     * @param baseMaxHit The base max hit for the powered staff's built-in spell before any
     *   modifiers are applied.
     */
    public fun getStaffMaxHit(
        player: Player,
        target: Player,
        baseMaxHit: Int,
        specialMultiplier: Double,
    ): Int {
        val maxHit = computeStaffMaxHit(player, target, baseMaxHit, specialMultiplier)
        player.maxHit = maxHit
        return maxHit
    }

    public fun computeStaffMaxHit(
        source: Player,
        target: Player,
        baseMaxHit: Int,
        specialMultiplier: Double,
    ): Int {
        val modifiedDamage = computeStaffModifiedDamage(source, baseMaxHit)
        val specMaxHit = (modifiedDamage * specialMultiplier).toInt()

        val defenceBonus = bonuses.defensiveMagicBonus(target)
        val reductionAttributes = reductions.collectPvP(target, random)

        return MaxHitOperations.applyDamageReductions(
            startDamage = specMaxHit,
            activeDefenceBonus = defenceBonus,
            reductionAttributes = reductionAttributes,
        )
    }

    public fun computeStaffModifiedDamage(source: Player, baseDamage: Int): Int {
        val magicDmgBonus = bonuses.magicDamageBonusBase(source)
        val prayerDmgBonus = MagicMaxHitOperations.getMagicDamagePrayerBonus(source)
        return MagicMaxHitOperations.modifyStaffBaseDamage(
            baseDamage = baseDamage,
            sourceBaseMagicDmgBonus = magicDmgBonus,
            sourceMagicPrayerBonus = prayerDmgBonus,
        )
    }
}
