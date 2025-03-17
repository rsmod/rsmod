package org.rsmod.api.combat.formulas.maxhit.melee

import jakarta.inject.Inject
import java.util.EnumSet
import org.rsmod.api.combat.commons.styles.MeleeAttackStyle
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.combat.formulas.attributes.CombatNpcAttributes
import org.rsmod.api.combat.formulas.attributes.CombatWornAttributes
import org.rsmod.api.combat.formulas.attributes.collector.CombatNpcAttributeCollector
import org.rsmod.api.combat.formulas.attributes.collector.MeleeWornAttributeCollector
import org.rsmod.api.combat.formulas.isSlayerTask
import org.rsmod.api.combat.maxhit.player.PlayerMeleeMaxHit
import org.rsmod.api.combat.weapon.WeaponSpeeds
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.bonus.WornBonuses
import org.rsmod.api.player.stat.stat
import org.rsmod.api.player.stat.statBase
import org.rsmod.api.player.vars.intVarp
import org.rsmod.api.random.GameRandom
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.type.npc.UnpackedNpcType

public class PvNMeleeMaxHit
@Inject
constructor(
    private val random: GameRandom,
    private val bonuses: WornBonuses,
    private val weaponSpeeds: WeaponSpeeds,
    private val npcCollector: CombatNpcAttributeCollector,
    private val wornCollector: MeleeWornAttributeCollector,
) {
    private var Player.maxHit by intVarp(varps.com_maxhit)

    /**
     * Computes the maximum melee hit for [player] against [target], applying the
     * [specialMultiplier] before passing the result to [modifyPostSpec].
     *
     * **Notes:**
     * - This function should be used instead of [computeMaxHit] in most cases to ensure consistency
     *   in max hit calculations. Future optimizations may depend on this function as the main entry
     *   point.
     * - The `com_maxhit` varp for [player] is updated with the computed max hit.
     */
    public fun getMaxHit(
        player: Player,
        target: Npc,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        specialMultiplier: Double,
    ): Int {
        val maxHit =
            computeMaxHit(
                source = player,
                target = target.visType,
                targetCurrHp = target.hitpoints,
                targetMaxHp = target.baseHitpointsLvl,
                attackType = attackType,
                attackStyle = attackStyle,
                specialMultiplier = specialMultiplier,
            )
        player.maxHit = maxHit
        return maxHit
    }

    public fun computeMaxHit(
        source: Player,
        target: UnpackedNpcType,
        targetCurrHp: Int,
        targetMaxHp: Int,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        specialMultiplier: Double,
    ): Int {
        val wornAttributes = wornCollector.collect(source, attackType)
        addProcAttributes(wornAttributes)

        val slayerTask = target.isSlayerTask(source)
        val npcAttributes = npcCollector.collect(target, targetCurrHp, targetMaxHp, slayerTask)

        val modifiedDamage =
            computeModifiedDamage(source, attackStyle, wornAttributes, npcAttributes)
        val specMaxHit = (modifiedDamage * specialMultiplier).toInt()
        return modifyPostSpec(source, specMaxHit, wornAttributes, npcAttributes)
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
        wornAttributes: EnumSet<CombatWornAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        val effectiveStrength =
            MeleeMaxHitOperations.calculateEffectiveStrength(source, attackStyle)
        val strengthBonus = bonuses.strengthBonus(source)
        val baseDamage = PlayerMeleeMaxHit.calculateBaseDamage(effectiveStrength, strengthBonus)
        return MeleeMaxHitOperations.modifyBaseDamage(baseDamage, wornAttributes, npcAttributes)
    }

    public fun modifyPostSpec(
        source: Player,
        modifiedDamage: Int,
        wornAttributes: EnumSet<CombatWornAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        val attackRate = weaponSpeeds.actual(source)
        val currHp = source.stat(stats.hitpoints)
        val maxHp = source.statBase(stats.hitpoints)
        return MeleeMaxHitOperations.modifyPostSpec(
            modifiedDamage,
            attackRate,
            currHp,
            maxHp,
            wornAttributes,
            npcAttributes,
        )
    }

    private fun addProcAttributes(attribs: EnumSet<CombatWornAttributes>) {
        if (attribs.containsKerisWeaponAttr() && random.randomBoolean(51)) {
            attribs += CombatWornAttributes.KerisProc
        }

        if (CombatWornAttributes.Gadderhammer in attribs && random.randomBoolean(20)) {
            attribs += CombatWornAttributes.GadderhammerProc
        }
    }

    private fun EnumSet<CombatWornAttributes>.containsKerisWeaponAttr(): Boolean =
        CombatWornAttributes.KerisWeapon in this ||
            CombatWornAttributes.KerisBreachPartisan in this ||
            CombatWornAttributes.KerisSunPartisan in this
}
