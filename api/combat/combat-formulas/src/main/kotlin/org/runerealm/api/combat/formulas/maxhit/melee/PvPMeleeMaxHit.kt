package org.runerealm.api.combat.formulas.maxhit.melee

import jakarta.inject.Inject
import java.util.EnumSet
import org.runerealm.api.combat.commons.styles.MeleeAttackStyle
import org.runerealm.api.combat.commons.types.MeleeAttackType
import org.runerealm.api.combat.formulas.attributes.CombatMeleeAttributes
import org.runerealm.api.combat.formulas.attributes.CombatNpcAttributes
import org.runerealm.api.combat.formulas.attributes.collector.CombatMeleeAttributeCollector
import org.runerealm.api.combat.formulas.attributes.collector.CombatNpcAttributeCollector
import org.runerealm.api.combat.formulas.attributes.collector.DamageReductionAttributeCollector
import org.runerealm.api.combat.formulas.maxhit.MaxHitOperations
import org.runerealm.api.combat.maxhit.player.PlayerMeleeMaxHit
import org.runerealm.api.combat.weapon.WeaponSpeeds
import org.runerealm.api.config.refs.varps
import org.runerealm.api.player.bonus.WornBonuses
import org.runerealm.api.player.stat.baseHitpointsLvl
import org.runerealm.api.player.stat.hitpoints
import org.runerealm.api.player.vars.intVarp
import org.runerealm.api.random.GameRandom
import org.runerealm.game.entity.Player

public class PvPMeleeMaxHit
@Inject
constructor(
    private val random: GameRandom,
    private val bonuses: WornBonuses,
    private val weaponSpeeds: WeaponSpeeds,
    private val npcAttributes: CombatNpcAttributeCollector,
    private val meleeAttributes: CombatMeleeAttributeCollector,
    private val reductions: DamageReductionAttributeCollector,
) {
    private var Player.maxHit by intVarp(varps.com_maxhit)

    /**
     * Computes the maximum melee hit for [player] against [target].
     *
     * **Notes:**
     * - This function should be used instead of [computeMaxHit] in most cases to ensure consistency
     *   in max hit calculations. Future optimizations may depend on this function as the main entry
     *   point.
     * - The `com_maxhit` varp for [player] is updated with the computed max hit.
     */
    public fun getMaxHit(
        player: Player,
        target: Player,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        specialMultiplier: Double,
    ): Int {
        val maxHit = computeMaxHit(player, target, attackType, attackStyle, specialMultiplier)
        player.maxHit = maxHit
        return maxHit
    }

    public fun computeMaxHit(
        source: Player,
        target: Player,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        specialMultiplier: Double,
    ): Int {
        val npcAttributes = EnumSet.noneOf(CombatNpcAttributes::class.java)
        val meleeAttributes = meleeAttributes.collect(source, attackType)

        val modifiedDamage =
            computeModifiedDamage(source, attackStyle, meleeAttributes, npcAttributes)
        val specMaxHit = (modifiedDamage * specialMultiplier).toInt()
        val postSpecDamage = modifyPostSpec(source, specMaxHit, meleeAttributes, npcAttributes)

        val reductionAttributes = reductions.collectPvP(target, random)
        return MaxHitOperations.applyDamageReductions(
            startDamage = postSpecDamage,
            activeDefenceBonus = null,
            reductionAttributes = reductionAttributes,
        )
    }

    /**
     * Computes and returns the modified base damage **before** applying [modifyPostSpec] or any
     * special attack multipliers.
     *
     * This is particularly useful for attacks like the `Voidwaker` special, where the **Magic**
     * attack is based on the **Melee** max hit. In this case, the melee max hit is used as a base,
     * but damage reductions (such as from `Corporeal Beast`) are **not** applied.
     */
    public fun computeModifiedDamage(
        source: Player,
        attackStyle: MeleeAttackStyle?,
        meleeAttributes: EnumSet<CombatMeleeAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        val effectiveStrength =
            MeleeMaxHitOperations.calculateEffectiveStrength(source, attackStyle)
        val strengthBonus = bonuses.strengthBonus(source)
        val baseDamage = PlayerMeleeMaxHit.calculateBaseDamage(effectiveStrength, strengthBonus)
        return MeleeMaxHitOperations.modifyBaseDamage(baseDamage, meleeAttributes, npcAttributes)
    }

    public fun modifyPostSpec(
        source: Player,
        modifiedDamage: Int,
        meleeAttributes: EnumSet<CombatMeleeAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        val attackRate = weaponSpeeds.actual(source)
        return MeleeMaxHitOperations.modifyPostSpec(
            modifiedDamage = modifiedDamage,
            attackRate = attackRate,
            currHp = source.hitpoints,
            maxHp = source.baseHitpointsLvl,
            meleeAttributes = meleeAttributes,
            npcAttributes = npcAttributes,
        )
    }
}
