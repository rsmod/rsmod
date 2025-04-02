package org.rsmod.api.combat.formulas.maxhit.ranged

import jakarta.inject.Inject
import java.util.EnumSet
import org.rsmod.api.combat.commons.styles.RangedAttackStyle
import org.rsmod.api.combat.commons.types.RangedAttackType
import org.rsmod.api.combat.formulas.attributes.CombatNpcAttributes
import org.rsmod.api.combat.formulas.attributes.CombatRangedAttributes
import org.rsmod.api.combat.formulas.attributes.collector.CombatRangedAttributeCollector
import org.rsmod.api.combat.formulas.attributes.collector.DamageReductionAttributeCollector
import org.rsmod.api.combat.formulas.maxhit.MaxHitOperations
import org.rsmod.api.combat.maxhit.player.PlayerRangedMaxHit
import org.rsmod.api.combat.weapon.WeaponSpeeds
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.bonus.WornBonuses
import org.rsmod.api.player.vars.intVarp
import org.rsmod.api.random.GameRandom
import org.rsmod.game.entity.Player

public class PvPRangedMaxHit
@Inject
constructor(
    private val random: GameRandom,
    private val bonuses: WornBonuses,
    private val weaponSpeeds: WeaponSpeeds,
    private val rangedAttributes: CombatRangedAttributeCollector,
    private val reductions: DamageReductionAttributeCollector,
) {
    private var Player.maxHit by intVarp(varps.com_maxhit)

    /**
     * Computes the maximum ranged hit for [player] against [target].
     *
     * **Notes:**
     * - This function should be used instead of [computeMaxHit] in most cases to ensure consistency
     *   in max hit calculations. Future optimizations may depend on this function as the main entry
     *   point.
     * - The `com_maxhit` varp for [player] is updated with the computed max hit.
     *
     * @param boltSpecDamage The additive bonus damage from bolt proc special attacks. For example,
     *   Opal bolts (e) special should set this value to `visible ranged level * 10%, rounded down`.
     */
    public fun getMaxHit(
        player: Player,
        target: Player,
        attackType: RangedAttackType?,
        attackStyle: RangedAttackStyle?,
        specialMultiplier: Double,
        boltSpecDamage: Int,
    ): Int {
        val maxHit =
            computeMaxHit(
                source = player,
                target = target,
                attackType = attackType,
                attackStyle = attackStyle,
                specialMultiplier = specialMultiplier,
                boltSpecDamage = boltSpecDamage,
            )
        player.maxHit = maxHit
        return maxHit
    }

    public fun computeMaxHit(
        source: Player,
        target: Player,
        attackType: RangedAttackType?,
        attackStyle: RangedAttackStyle?,
        specialMultiplier: Double,
        boltSpecDamage: Int,
    ): Int {
        val npcAttributes = EnumSet.noneOf(CombatNpcAttributes::class.java)
        val rangeAttributes = rangedAttributes.collect(source, attackType, attackStyle)

        val modifiedDamage = computeModifiedDamage(source, attackStyle)
        val specMaxHit = (modifiedDamage * specialMultiplier).toInt()
        val finalMaxHit =
            modifyPostSpec(source, specMaxHit, boltSpecDamage, rangeAttributes, npcAttributes)

        val reductionAttributes = reductions.collect(target, pvp = true, random)
        return MaxHitOperations.applyDamageReductions(finalMaxHit, null, reductionAttributes)
    }

    // Note: This currently does _not_ take modifiers into account.
    // For max hits, all modifiers - except the Twisted bow - are restricted behind npc attributes.
    // If a modifier is ever found to work against both npcs and players, we will need to revisit
    // this. In the worst case, we can add a new function to `RangedMaxHitOperations` that does
    // not rely on npc attributes, and use it for pvp.
    public fun computeModifiedDamage(source: Player, attackStyle: RangedAttackStyle?): Int {
        val effectiveRanged = RangedMaxHitOperations.calculateEffectiveRanged(source, attackStyle)
        val rangedBonus = bonuses.rangedStrengthBonus(source)
        return PlayerRangedMaxHit.calculateBaseDamage(effectiveRanged, rangedBonus)
    }

    public fun modifyPostSpec(
        source: Player,
        modifiedDamage: Int,
        boltSpecDamage: Int,
        rangeAttributes: EnumSet<CombatRangedAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        val attackRate = weaponSpeeds.actual(source)
        return RangedMaxHitOperations.modifyPostSpec(
            modifiedDamage = modifiedDamage,
            boltSpecDamage = boltSpecDamage,
            attackRate = attackRate,
            rangeAttributes = rangeAttributes,
            npcAttributes = npcAttributes,
        )
    }
}
