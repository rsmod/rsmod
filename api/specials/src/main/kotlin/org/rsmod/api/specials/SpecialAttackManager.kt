package org.rsmod.api.specials

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.combat.commons.styles.MeleeAttackStyle
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.combat.manager.CombatAttackManager
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.specials.energy.SpecialAttackEnergy
import org.rsmod.api.specials.weapon.SpecialAttackWeapons
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.Player
import org.rsmod.game.hit.Hit
import org.rsmod.game.type.obj.ObjType

public class SpecialAttackManager
@Inject
constructor(
    private val energy: SpecialAttackEnergy,
    private val weapons: SpecialAttackWeapons,
    private val manager: CombatAttackManager,
) {
    public fun hasSpecialEnergy(source: ProtectedAccess, energyInHundreds: Int): Boolean {
        return energy.hasSpecialEnergy(source.player, energyInHundreds)
    }

    public fun takeSpecialEnergy(source: ProtectedAccess, energyInHundreds: Int) {
        energy.takeSpecialEnergy(source.player, energyInHundreds)
    }

    public fun getSpecialEnergyRequirement(obj: ObjType): Int? = weapons.getSpecialEnergy(obj)

    /** @see [CombatAttackManager.setNextAttackDelay] */
    public fun setNextAttackDelay(source: ProtectedAccess, cycles: Int) {
        manager.setNextAttackDelay(source, cycles)
    }

    /** @see [CombatAttackManager.continueCombat] */
    public fun continueCombat(source: ProtectedAccess, target: PathingEntity) {
        manager.continueCombat(source, target)
    }

    /** @see [CombatAttackManager.continueCombat] */
    public fun continueCombat(source: ProtectedAccess, target: Npc) {
        manager.continueCombat(source, target)
    }

    /** @see [CombatAttackManager.continueCombat] */
    public fun continueCombat(source: ProtectedAccess, target: Player) {
        manager.continueCombat(source, target)
    }

    /**
     * Cancels the combat interaction while keeping the associated [Player.actionDelay] set to the
     * preset delay determined by the combat weapon's attack rate.
     *
     * This ensures that the player's action delay remains consistent with their last attack.
     *
     * This should be used when a special attack was performed successfully. If the special attack
     * could not be performed, consider using [clearCombat] instead.
     *
     * **Important Note:** When calling this function, ensure that the `attack` function returns
     * `true`. This signals to the combat script that the special attack was properly handled. If
     * `attack` returns `false`, the regular combat attack will still be processed **for one cycle**
     * because the combat script is already in progress. However, after that cycle, the interaction
     * will become invalid and will not execute again.
     *
     * @see [clearCombat]
     */
    public fun stopCombat(source: ProtectedAccess): Unit = manager.stopCombat(source)

    /**
     * Similar to [stopCombat], but resets the associated [Player.actionDelay] to the current map
     * clock.
     *
     * This should be used when a special attack could not be performed and the combat interaction
     * needs to be terminated. Since the player did not actually attack, their action delay should
     * be updated to reflect that.
     *
     * **Important Note:** When calling this function, ensure that the `attack` function returns
     * `true`. This signals to the combat script that the special attack was properly handled. If
     * `attack` returns `false`, the regular combat attack will still be processed **for one cycle**
     * because the combat script is already in progress. However, after that cycle, the interaction
     * will become invalid and will not execute again.
     */
    public fun clearCombat(source: ProtectedAccess): Unit = manager.clearCombat(source)

    /** @see [CombatAttackManager.rollMeleeDamage] */
    public fun rollMeleeDamage(
        source: ProtectedAccess,
        target: PathingEntity,
        attack: CombatAttack.Melee,
        accuracyBoost: Int,
        damageBoost: Int,
        attackType: MeleeAttackType? = attack.type,
        attackStyle: MeleeAttackStyle? = attack.style,
        blockType: MeleeAttackType? = attack.type,
    ): Int =
        manager.rollMeleeDamage(
            source,
            target,
            attack,
            accuracyBoost,
            damageBoost,
            attackType,
            attackStyle,
            blockType,
        )

    /** @see [CombatAttackManager.rollMeleeAccuracy] */
    public fun rollMeleeAccuracy(
        source: ProtectedAccess,
        target: PathingEntity,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        blockType: MeleeAttackType?,
        percentBoost: Int,
    ): Boolean =
        manager.rollMeleeAccuracy(source, target, attackType, attackStyle, blockType, percentBoost)

    /** @see [CombatAttackManager.rollMeleeMaxHit] */
    public fun rollMeleeMaxHit(
        source: ProtectedAccess,
        target: PathingEntity,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        percentBoost: Int,
    ): Int = manager.rollMeleeMaxHit(source, target, attackType, attackStyle, percentBoost)

    /** @see [CombatAttackManager.calculateMeleeMaxHit] */
    public fun calculateMeleeMaxHit(
        source: ProtectedAccess,
        target: PathingEntity,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        percentBoost: Int,
    ): Int = manager.calculateMeleeMaxHit(source, target, attackType, attackStyle, percentBoost)

    /** @see [CombatAttackManager.queueMeleeHit] */
    public fun queueMeleeHit(
        source: ProtectedAccess,
        target: PathingEntity,
        damage: Int,
        delay: Int,
    ): Hit = manager.queueMeleeHit(source, target, damage, delay)
}
