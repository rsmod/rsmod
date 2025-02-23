package org.rsmod.api.specials

import jakarta.inject.Inject
import org.rsmod.api.specials.configs.SpecialAttackEnergyEnums
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
     * [SpecialAttackEnergyEnums.energy_requirements] and is deducted from the player's special
     * attack energy when used.
     *
     * This does not account for additional energy costs that some special attacks impose. For
     * example, the Dragon hasta's special attack may consume more than the standard required
     * amount. Any extra energy cost can be managed via [SpecialAttackMap.takeSpecialEnergy].
     *
     * Throws an [IllegalStateException] if [specWeapon] is already registered with any special
     * attack.
     *
     * @see [SpecialAttackMap.hasSpecialEnergy]
     * @see [SpecialAttackMap.takeSpecialEnergy]
     */
    public fun registerInstant(specWeapon: ObjType, special: InstantSpecialAttack) {
        val result = registry.add(specWeapon, special)
        assertValidResult(specWeapon, result)
    }

    /**
     * Registers the [specWeapon] special attack ([special]) as a [CombatSpecialAttack], which
     * activates on the player's next attack in combat.
     *
     * The special attack energy requirement is determined by
     * [SpecialAttackEnergyEnums.energy_requirements] and is deducted from the player's special
     * attack energy when used.
     *
     * This does not account for additional energy costs that some special attacks impose. For
     * example, the Dragon hasta's special attack may consume more than the standard required
     * amount. Any extra energy cost can be managed via [SpecialAttackMap.takeSpecialEnergy].
     *
     * Throws an [IllegalStateException] if [specWeapon] is already registered with any special
     * attack.
     *
     * @see [SpecialAttackMap.hasSpecialEnergy]
     * @see [SpecialAttackMap.takeSpecialEnergy]
     */
    public fun registerAttack(specWeapon: ObjType, special: CombatSpecialAttack) {
        val result = registry.add(specWeapon, special)
        assertValidResult(specWeapon, result)
    }

    /**
     * Registers the [specWeapon] special attacks ([playerSpecial] and [npcSpecial]) as a
     * [CombatSpecialAttack], which activates on the player's next attack in combat.
     *
     * This function is a convenience wrapper that combines [playerSpecial] and [npcSpecial] into a
     * single [CombatSpecialAttack] via [CombatSpecialAttack.from] before registering it.
     *
     * The special attack energy requirement is determined by
     * [SpecialAttackEnergyEnums.energy_requirements] and is deducted from the player's special
     * attack energy when used.
     *
     * This does not account for additional energy costs that some special attacks impose. For
     * example, the Dragon hasta's special attack may consume more than the standard required
     * amount. Any extra energy cost can be managed via [SpecialAttackMap.takeSpecialEnergy].
     *
     * Throws an [IllegalStateException] if [specWeapon] is already registered with any special
     * attack.
     *
     * @see [SpecialAttackMap.hasSpecialEnergy]
     * @see [SpecialAttackMap.takeSpecialEnergy]
     */
    public fun registerAttack(
        specWeapon: ObjType,
        playerSpecial: CombatSpecialAttack.PlayerSpecific,
        npcSpecial: CombatSpecialAttack.NpcSpecific,
    ) {
        val combatSpec = CombatSpecialAttack.from(playerSpecial, npcSpecial)
        registerAttack(specWeapon, combatSpec)
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
