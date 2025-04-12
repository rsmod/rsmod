package org.rsmod.api.specials.combat

import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.specials.SpecialAttackManager
import org.rsmod.api.specials.energy.SpecialAttackEnergy
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player

public interface CombatSpecialAttack<T : CombatAttack> {
    /**
     * Executes this special attack against an [Npc] target.
     *
     * #### Important Notes:
     * - This function is invoked **before** any special attack energy is deducted.
     * - Return `true` to allow the engine to deduct energy afterward.
     * - If `false` is returned, no energy will be deducted.
     * - If the weapon has a specialized requirement (e.g., Soulreaper Axe), the engine will **not**
     *   perform automatic energy checks or deductions. It is the responsibility of this function to
     *   validate such conditions.
     * - Any additional energy costs beyond the standard value can be deducted manually via
     *   [SpecialAttackManager.takeSpecialEnergy].
     * - If you intend to cancel the combat interaction, you **must** do so explicitly (e.g., via
     *   [SpecialAttackManager.stopCombat]); otherwise, the interaction may linger.
     * - Likewise, if you want the interaction to continue, call
     *   [SpecialAttackManager.continueCombat].
     *
     * @see [SpecialAttackEnergy.isSpecializedRequirement]
     */
    public suspend fun ProtectedAccess.attack(target: Npc, attack: T): Boolean

    /**
     * Executes this special attack against a [Player] target.
     *
     * #### Important Notes:
     * - This function is invoked **before** any special attack energy is deducted.
     * - Return `true` to allow the engine to deduct energy afterward.
     * - If `false` is returned, no energy will be deducted.
     * - If the weapon has a specialized requirement (e.g., Soulreaper Axe), the engine will **not**
     *   perform automatic energy checks or deductions. It is the responsibility of this function to
     *   validate such conditions.
     * - Any additional energy costs beyond the standard value can be deducted manually via
     *   [SpecialAttackManager.takeSpecialEnergy].
     * - If you intend to cancel the combat interaction, you **must** do so explicitly (e.g., via
     *   [SpecialAttackManager.stopCombat]); otherwise, the interaction may linger.
     * - Likewise, if you want the interaction to continue, call
     *   [SpecialAttackManager.continueCombat].
     *
     * @see [SpecialAttackEnergy.isSpecializedRequirement]
     */
    public suspend fun ProtectedAccess.attack(target: Player, attack: T): Boolean
}

public interface MeleeSpecialAttack : CombatSpecialAttack<CombatAttack.Melee>

public interface RangedSpecialAttack : CombatSpecialAttack<CombatAttack.Ranged>

public interface MagicSpecialAttack : CombatSpecialAttack<CombatAttack.Staff>
