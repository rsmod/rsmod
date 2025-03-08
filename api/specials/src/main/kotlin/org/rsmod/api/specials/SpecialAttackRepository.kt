package org.rsmod.api.specials

import jakarta.inject.Inject
import org.rsmod.api.specials.combat.MeleeSpecialAttack
import org.rsmod.api.specials.combat.RangedSpecialAttack
import org.rsmod.api.specials.configs.SpecialAttackEnergyEnums
import org.rsmod.api.specials.instant.InstantSpecialAttack
import org.rsmod.api.specials.weapon.SpecialAttackWeapons
import org.rsmod.game.type.obj.ObjType

public class SpecialAttackRepository
@Inject
constructor(private val registry: SpecialAttackRegistry) {
    /**
     * Registers the [specWeapon] special attack ([special]) as an [InstantSpecialAttack], which
     * triggers immediately when the player enables it.
     *
     * The special attack energy requirement is determined by
     * [SpecialAttackWeapons.getSpecialEnergy] and is deducted from the player's special attack
     * energy when used.
     *
     * @throws IllegalStateException if [specWeapon] is already registered with any special attack.
     */
    public fun registerInstant(specWeapon: ObjType, special: InstantSpecialAttack) {
        val result = registry.add(specWeapon, special)
        assertValidResult(specWeapon, result)
    }

    /**
     * Registers the [specWeapon] special attack ([special]) as a [MeleeSpecialAttack], which
     * activates on the player's next melee-based attack in combat.
     *
     * The combat style in use _before_ the special activates determines whether it is a
     * "melee-based" special attack. For example, a Voidwaker special remains melee-based even
     * though it deals magic damage.
     *
     * ### Important Note
     *
     * The special attack energy requirement is determined by
     * [SpecialAttackWeapons.getSpecialEnergy] and is deducted from the player's special attack
     * energy when used.
     *
     * If the special attack energy requirement is defined as less than `10` (with the standard max
     * energy being `1000`), this indicates a specialized requirement that is **not** automatically
     * checked or deducted before activation.
     *
     * For example, the Soulreaper Axe has a requirement of `1`, meaning the engine will **not**
     * deduct or check it automatically. Instead, it must be validated within [special], returning
     * `false` if the player lacks the required soul stacks.
     *
     * If `special` returns `false`, the player's normal combat attack will proceed as if the
     * special attack was never activated.
     *
     * ### Additional Energy Costs
     *
     * Some special attacks may impose additional energy costs beyond the standard requirement. For
     * example, the Dragon hasta's special attack may consume more energy than usual. Any extra
     * energy cost can be managed via [SpecialAttackManager.takeSpecialEnergy].
     *
     * @throws IllegalStateException if [specWeapon] is already registered with any special attack.
     * @see [SpecialAttackManager.hasSpecialEnergy]
     * @see [SpecialAttackManager.takeSpecialEnergy]
     */
    public fun registerMelee(specWeapon: ObjType, special: MeleeSpecialAttack) {
        val result = registry.add(specWeapon, special)
        assertValidResult(specWeapon, result)
    }

    /**
     * Registers the [specWeapon] special attack ([special]) as a [RangedSpecialAttack], which
     * activates on the player's next ranged-based attack in combat.
     *
     * The combat style in use _before_ the special activates determines whether it is a
     * "ranged-based" special attack.
     *
     * ### Important Note
     *
     * The special attack energy requirement is determined by
     * [SpecialAttackWeapons.getSpecialEnergy] and is deducted from the player's special attack
     * energy when used.
     *
     * If the special attack energy requirement is defined as less than `10` (with the standard max
     * energy being `1000`), this indicates a specialized requirement that is **not** automatically
     * checked or deducted before activation.
     *
     * For example, the Soulreaper Axe has a requirement of `1`, meaning the engine will **not**
     * deduct or check it automatically. Instead, it must be validated within [special], returning
     * `false` if the player lacks the required soul stacks.
     *
     * If `special` returns `false`, the player's normal combat attack will proceed as if the
     * special attack was never activated.
     *
     * ### Additional Energy Costs
     *
     * Some special attacks may impose additional energy costs beyond the standard requirement. For
     * example, the Dragon hasta's special attack may consume more energy than usual. Any extra
     * energy cost can be managed via [SpecialAttackManager.takeSpecialEnergy].
     *
     * @throws IllegalStateException if [specWeapon] is already registered with any special attack.
     * @see [SpecialAttackManager.hasSpecialEnergy]
     * @see [SpecialAttackManager.takeSpecialEnergy]
     */
    public fun registerRanged(specWeapon: ObjType, special: RangedSpecialAttack) {
        val result = registry.add(specWeapon, special)
        assertValidResult(specWeapon, result)
    }

    private fun assertValidResult(specWeapon: ObjType, result: SpecialAttackRegistry.Result.Add) {
        when (result) {
            SpecialAttackRegistry.Result.Add.AlreadyAdded -> {
                error("Weapon already has a special attack mapped: $specWeapon")
            }
            SpecialAttackRegistry.Result.Add.SpecialEnergyNotMapped -> {
                error(
                    "Weapon `$specWeapon` was not found in the required enums. " +
                        "Use [${SpecialAttackWeapons::class}] " +
                        "and [${SpecialAttackEnergyEnums::class}] " +
                        "as reference for which enums are required."
                )
            }
            SpecialAttackRegistry.Result.Add.Success -> {
                /* no-op */
            }
        }
    }
}
