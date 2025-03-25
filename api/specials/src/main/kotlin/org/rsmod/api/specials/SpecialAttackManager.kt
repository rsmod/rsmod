package org.rsmod.api.specials

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.combat.commons.styles.MeleeAttackStyle
import org.rsmod.api.combat.commons.styles.RangedAttackStyle
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.combat.commons.types.RangedAttackType
import org.rsmod.api.combat.manager.PlayerAttackManager
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.specials.energy.SpecialAttackEnergy
import org.rsmod.api.specials.weapon.SpecialAttackWeapons
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.Player
import org.rsmod.game.hit.Hit
import org.rsmod.game.proj.ProjAnim
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.proj.ProjAnimType
import org.rsmod.game.type.spot.SpotanimType
import org.rsmod.game.type.synth.SynthType

public class SpecialAttackManager
@Inject
constructor(
    private val energy: SpecialAttackEnergy,
    private val weapons: SpecialAttackWeapons,
    private val manager: PlayerAttackManager,
) {
    public fun hasSpecialEnergy(source: ProtectedAccess, energyInHundreds: Int): Boolean {
        return energy.hasSpecialEnergy(source.player, energyInHundreds)
    }

    public fun takeSpecialEnergy(source: ProtectedAccess, energyInHundreds: Int) {
        energy.takeSpecialEnergy(source.player, energyInHundreds)
    }

    public fun getSpecialEnergyRequirement(obj: ObjType): Int? = weapons.getSpecialEnergy(obj)

    /**
     * Sets [Player.actionDelay] to the current map clock + [cycles].
     *
     * This determines the delay before the player's next attack (after the current one completes).
     * For example, setting [cycles] to `2` allows the player to perform their next attack in `2`
     * cycles.
     *
     * **Note:** Systems that use this special attack manager should preemptively apply the delay
     * based on the `attackrate` of the weapon being used, eliminating the need for manual
     * adjustments. This function is intended for special cases where fine-tuning or custom attack
     * delays are required after a special attack.
     *
     * @param cycles The number of cycles to wait before the next attack can be performed.
     */
    public fun setNextAttackDelay(access: ProtectedAccess, cycles: Int) {
        manager.setNextAttackDelay(access.player, cycles)
    }

    /** @see [PlayerAttackManager.continueCombat] */
    public fun continueCombat(source: ProtectedAccess, target: PathingEntity) {
        manager.continueCombat(source.player, target)
    }

    /** @see [PlayerAttackManager.continueCombat] */
    public fun continueCombat(source: ProtectedAccess, target: Npc) {
        manager.continueCombat(source.player, target)
    }

    /** @see [PlayerAttackManager.continueCombat] */
    public fun continueCombat(source: ProtectedAccess, target: Player) {
        manager.continueCombat(source.player, target)
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
    public fun stopCombat(access: ProtectedAccess): Unit = manager.stopCombat(access.player)

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
    public fun clearCombat(access: ProtectedAccess): Unit = manager.clearCombat(access.player)

    /** @see [PlayerAttackManager.rollMeleeDamage] */
    public fun rollMeleeDamage(
        source: ProtectedAccess,
        target: PathingEntity,
        attack: CombatAttack.Melee,
        accuracyBoost: Int,
        maxHitBoost: Int,
        attackType: MeleeAttackType? = attack.type,
        attackStyle: MeleeAttackStyle? = attack.style,
        blockType: MeleeAttackType? = attack.type,
    ): Int =
        manager.rollMeleeDamage(
            source = source.player,
            target = target,
            attack = attack,
            accuracyBoost = accuracyBoost,
            maxHitBoost = maxHitBoost,
            attackType = attackType,
            attackStyle = attackStyle,
            blockType = blockType,
        )

    /** @see [PlayerAttackManager.rollMeleeAccuracy] */
    public fun rollMeleeAccuracy(
        source: ProtectedAccess,
        target: PathingEntity,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        blockType: MeleeAttackType?,
        percentBoost: Int,
    ): Boolean =
        manager.rollMeleeAccuracy(
            source = source.player,
            target = target,
            attackType = attackType,
            attackStyle = attackStyle,
            blockType = blockType,
            percentBoost = percentBoost,
        )

    /** @see [PlayerAttackManager.rollMeleeMaxHit] */
    public fun rollMeleeMaxHit(
        source: ProtectedAccess,
        target: PathingEntity,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        percentBoost: Int,
    ): Int = manager.rollMeleeMaxHit(source.player, target, attackType, attackStyle, percentBoost)

    /** @see [PlayerAttackManager.calculateMeleeMaxHit] */
    public fun calculateMeleeMaxHit(
        source: ProtectedAccess,
        target: PathingEntity,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        percentBoost: Int,
    ): Int =
        manager.calculateMeleeMaxHit(source.player, target, attackType, attackStyle, percentBoost)

    /** @see [PlayerAttackManager.queueMeleeHit] */
    public fun queueMeleeHit(
        source: ProtectedAccess,
        target: PathingEntity,
        damage: Int,
        delay: Int = 1,
    ): Hit = manager.queueMeleeHit(source.player, target, damage, delay)

    /** @see [PlayerAttackManager.rollRangedDamage] */
    public fun rollRangedDamage(
        source: ProtectedAccess,
        target: PathingEntity,
        attack: CombatAttack.Ranged,
        accuracyBoost: Int = 0,
        maxHitBoost: Int = 0,
        attackType: RangedAttackType? = attack.type,
        attackStyle: RangedAttackStyle? = attack.style,
        blockType: RangedAttackType? = attack.type,
        boltSpecDamage: Int = 0,
    ): Int =
        manager.rollRangedDamage(
            source = source.player,
            target = target,
            attack = attack,
            accuracyBoost = accuracyBoost,
            maxHitBoost = maxHitBoost,
            attackType = attackType,
            attackStyle = attackStyle,
            blockType = blockType,
            boltSpecDamage = boltSpecDamage,
        )

    /** @see [PlayerAttackManager.rollRangedAccuracy] */
    public fun rollRangedAccuracy(
        source: ProtectedAccess,
        target: PathingEntity,
        attackType: RangedAttackType?,
        attackStyle: RangedAttackStyle?,
        blockType: RangedAttackType?,
        percentBoost: Int,
    ): Boolean =
        manager.rollRangedAccuracy(
            source = source.player,
            target = target,
            attackType = attackType,
            attackStyle = attackStyle,
            blockType = blockType,
            percentBoost = percentBoost,
        )

    /** @see [PlayerAttackManager.rollRangedMaxHit] */
    public fun rollRangedMaxHit(
        source: ProtectedAccess,
        target: PathingEntity,
        attackType: RangedAttackType?,
        attackStyle: RangedAttackStyle?,
        percentBoost: Int,
        boltSpecDamage: Int,
    ): Int =
        manager.rollRangedMaxHit(
            source = source.player,
            target = target,
            attackType = attackType,
            attackStyle = attackStyle,
            percentBoost = percentBoost,
            boltSpecDamage = boltSpecDamage,
        )

    /** @see [PlayerAttackManager.calculateRangedMaxHit] */
    public fun calculateRangedMaxHit(
        source: ProtectedAccess,
        target: PathingEntity,
        attackType: RangedAttackType?,
        attackStyle: RangedAttackStyle?,
        percentBoost: Int,
        boltSpecDamage: Int,
    ): Int =
        manager.calculateRangedMaxHit(
            source = source.player,
            target = target,
            attackType = attackType,
            attackStyle = attackStyle,
            percentBoost = percentBoost,
            boltSpecDamage = boltSpecDamage,
        )

    /** @see [PlayerAttackManager.queueRangedHit] */
    public fun queueRangedHit(
        source: ProtectedAccess,
        target: PathingEntity,
        ammo: ObjType?,
        damage: Int,
        clientDelay: Int,
        hitDelay: Int = 1 + (clientDelay / 30),
    ): Hit =
        manager.queueRangedHit(
            source = source.player,
            target = target,
            ammo = ammo,
            damage = damage,
            clientDelay = clientDelay,
            hitDelay = hitDelay,
        )

    /** @see [PlayerAttackManager.queueRangedProjectileHit] */
    public fun queueRangedProjectileHit(
        source: ProtectedAccess,
        target: PathingEntity,
        ammo: ObjType?,
        damage: Int,
        proj: ProjAnim,
    ): Hit = manager.queueRangedProjectileHit(source.player, target, ammo, damage, proj)

    /** @see [PlayerAttackManager.queueRangedDamage] */
    public fun queueRangedDamage(
        source: ProtectedAccess,
        target: PathingEntity,
        ammo: ObjType?,
        damage: Int,
        hitDelay: Int,
    ): Hit =
        manager.queueRangedDamage(
            source = source.player,
            target = target,
            ammo = ammo,
            damage = damage,
            hitDelay = hitDelay,
        )

    public fun spawnProjectile(
        source: ProtectedAccess,
        target: PathingEntity,
        spotanim: SpotanimType,
        projanim: ProjAnimType,
    ): ProjAnim = manager.spawnProjectile(source.player, target, spotanim, projanim)

    public fun soundArea(
        source: PathingEntity,
        synth: SynthType,
        delay: Int = 0,
        loops: Int = 1,
        radius: Int = 5,
        size: Int = 0,
    ): Unit =
        manager.soundArea(
            source.coords,
            synth = synth,
            delay = delay,
            loops = loops,
            radius = radius,
            size = size,
        )
}
