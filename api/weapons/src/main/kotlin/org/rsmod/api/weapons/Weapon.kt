package org.rsmod.api.weapons

import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player

public interface Weapon<T : CombatAttack> {
    /**
     * Executes this weapon's attack against an [Npc] target.
     *
     * **Note:** If you intend to cancel the combat interaction, you **must** do so explicitly -
     * call [WeaponAttackManager.stopCombat]. Likewise, to continue the interaction, call
     * [WeaponAttackManager.continueCombat].
     *
     * @return `true` if this weapon handled the attack and the default combat flow should stop;
     *   `false` to resume the previously interrupted default combat attack. This should usually
     *   return `true`.
     */
    public suspend fun ProtectedAccess.attack(target: Npc, attack: T): Boolean

    /**
     * Executes this weapon's attack against a [Player] target.
     *
     * **Note:** If you intend to cancel the combat interaction, you **must** do so explicitly -
     * call [WeaponAttackManager.stopCombat]. Likewise, to continue the interaction, call
     * [WeaponAttackManager.continueCombat].
     *
     * @return `true` if this weapon handled the attack and the default combat flow should stop;
     *   `false` to resume the previously interrupted default combat attack. This should usually
     *   return `true`.
     */
    public suspend fun ProtectedAccess.attack(target: Player, attack: T): Boolean
}

public suspend fun <T : CombatAttack> Weapon<T>.attack(
    access: ProtectedAccess,
    target: Player,
    attack: T,
): Boolean = access.attack(target, attack)

public suspend fun <T : CombatAttack> Weapon<T>.attack(
    access: ProtectedAccess,
    target: Npc,
    attack: T,
): Boolean = access.attack(target, attack)

public interface MeleeWeapon : Weapon<CombatAttack.Melee>

public interface RangedWeapon : Weapon<CombatAttack.Ranged>

public interface MagicWeapon : Weapon<CombatAttack.Staff>
