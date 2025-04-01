package org.rsmod.api.combat.formulas.maxhit.magic

import jakarta.inject.Inject
import java.util.EnumSet
import org.rsmod.api.combat.commons.magic.Spellbook
import org.rsmod.api.combat.formulas.attributes.CombatNpcAttributes
import org.rsmod.api.combat.formulas.attributes.CombatSpellAttributes
import org.rsmod.api.combat.formulas.attributes.CombatStaffAttributes
import org.rsmod.api.combat.formulas.attributes.collector.CombatMagicAttributeCollector
import org.rsmod.api.combat.formulas.attributes.collector.CombatNpcAttributeCollector
import org.rsmod.api.combat.formulas.isSlayerTask
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.bonus.WornBonuses
import org.rsmod.api.player.stat.magicLvl
import org.rsmod.api.player.vars.intVarp
import org.rsmod.api.random.GameRandom
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.game.type.obj.ObjType

public class PvNMagicMaxHit
@Inject
constructor(
    private val random: GameRandom,
    private val bonuses: WornBonuses,
    private val npcAttributes: CombatNpcAttributeCollector,
    private val magicAttributes: CombatMagicAttributeCollector,
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
     * @param attackRate The number of server cycles between each spell attack. This is usually `5`.
     * @param usedSunfireRune Whether the player used a Sunfire rune for this cast.
     */
    public fun getSpellMaxHit(
        player: Player,
        target: Npc,
        spell: ObjType,
        spellbook: Spellbook?,
        baseMaxHit: Int,
        attackRate: Int,
        usedSunfireRune: Boolean,
    ): IntRange {
        val targetType = target.visType
        val elementalWeakness = targetType.param(params.elemental_weakness_percent)
        val maxHit =
            computeSpellMaxHit(
                source = player,
                target = targetType,
                spell = spell,
                targetCurrHp = target.hitpoints,
                targetMaxHp = target.baseHitpointsLvl,
                targetWeaknessPercent = elementalWeakness,
                baseMaxHit = baseMaxHit,
                attackRate = attackRate,
                spellbook = spellbook,
                usedSunfireRune = usedSunfireRune,
            )
        player.maxHit = maxHit.last
        return maxHit
    }

    public fun computeSpellMaxHit(
        source: Player,
        target: UnpackedNpcType,
        spell: ObjType,
        targetCurrHp: Int,
        targetMaxHp: Int,
        targetWeaknessPercent: Int,
        baseMaxHit: Int,
        attackRate: Int,
        spellbook: Spellbook?,
        usedSunfireRune: Boolean,
    ): IntRange {
        val spellAttributes =
            magicAttributes.spellCollect(source, spell, spellbook, usedSunfireRune, random)

        val slayerTask = target.isSlayerTask(source)
        val npcAttributes = npcAttributes.collect(target, targetCurrHp, targetMaxHp, slayerTask)

        val modifiedDamage =
            computeSpellModifiedDamage(source, baseMaxHit, spellAttributes, npcAttributes)
        return modifySpellPostSpec(
            modifiedDamage = modifiedDamage,
            baseDamage = baseMaxHit,
            attackRate = attackRate,
            targetWeaknessPercent = targetWeaknessPercent,
            spellAttributes = spellAttributes,
            npcAttributes = npcAttributes,
        )
    }

    public fun computeSpellModifiedDamage(
        source: Player,
        baseDamage: Int,
        spellAttributes: EnumSet<CombatSpellAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        val magicDmgBonus = bonuses.magicDamageBonusBase(source)
        val prayerDmgBonus = MagicMaxHitOperations.getMagicDamagePrayerBonus(source)
        return MagicMaxHitOperations.modifySpellBaseDamage(
            baseDamage = baseDamage,
            sourceMagic = source.magicLvl,
            sourceBaseMagicDmgBonus = magicDmgBonus,
            sourceMagicPrayerBonus = prayerDmgBonus,
            spellAttributes = spellAttributes,
            npcAttributes = npcAttributes,
        )
    }

    public fun modifySpellPostSpec(
        modifiedDamage: Int,
        baseDamage: Int,
        attackRate: Int,
        targetWeaknessPercent: Int,
        spellAttributes: EnumSet<CombatSpellAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): IntRange =
        MagicMaxHitOperations.modifySpellPostSpec(
            modifiedDamage = modifiedDamage,
            baseDamage = baseDamage,
            attackRate = attackRate,
            targetWeaknessPercent = targetWeaknessPercent,
            spellAttributes = spellAttributes,
            npcAttributes = npcAttributes,
        )

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
    public fun getStaffMaxHit(player: Player, target: Npc, baseMaxHit: Int): Int {
        val maxHit =
            computeStaffMaxHit(
                source = player,
                target = target.visType,
                targetCurrHp = target.hitpoints,
                targetMaxHp = target.baseHitpointsLvl,
                baseMaxHit = baseMaxHit,
            )
        player.maxHit = maxHit
        return maxHit
    }

    public fun computeStaffMaxHit(
        source: Player,
        target: UnpackedNpcType,
        targetCurrHp: Int,
        targetMaxHp: Int,
        baseMaxHit: Int,
    ): Int {
        val staffAttributes = magicAttributes.staffCollect(source, random)

        val slayerTask = target.isSlayerTask(source)
        val npcAttributes = npcAttributes.collect(target, targetCurrHp, targetMaxHp, slayerTask)

        return computeStaffModifiedDamage(source, baseMaxHit, staffAttributes, npcAttributes)
    }

    public fun computeStaffModifiedDamage(
        source: Player,
        baseDamage: Int,
        staffAttributes: EnumSet<CombatStaffAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        val magicDmgBonus = bonuses.magicDamageBonusBase(source)
        val prayerDmgBonus = MagicMaxHitOperations.getMagicDamagePrayerBonus(source)
        return MagicMaxHitOperations.modifyStaffBaseDamage(
            baseDamage = baseDamage,
            sourceBaseMagicDmgBonus = magicDmgBonus,
            sourceMagicPrayerBonus = prayerDmgBonus,
            staffAttributes = staffAttributes,
            npcAttributes = npcAttributes,
        )
    }
}
