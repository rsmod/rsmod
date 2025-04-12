package org.rsmod.api.specials.instant

import org.rsmod.api.player.protect.ProtectedAccess

/**
 * A special attack that activates immediately when the player enables it (i.e., by clicking the
 * special attack button).
 *
 * The special attack energy is **not** deducted until after [activate] returns `true`. This allows
 * the implementation to perform any validation before committing to the special attack, such as
 * checking for charges or area-based restrictions.
 *
 * If the special attack energy requirement is defined as less than `10` (with the standard max
 * energy being `1000`), this indicates a specialized requirement that is **not** automatically
 * checked or deducted on activation. It is the responsibility of the implementation to handle this
 * logic manually.
 *
 * @see [org.rsmod.api.specials.SpecialAttackRepository.registerInstant]
 */
public fun interface InstantSpecialAttack {
    /**
     * Executes the logic for an [InstantSpecialAttack] when the player activates it.
     *
     * This function is invoked immediately when the player attempts to use the special attack. At
     * the time of execution, the player's special attack energy has **not** yet been deducted.
     *
     * It is the responsibility of the implementation to determine whether the special attack can
     * proceed (e.g., checking charges, conditions, cooldowns). If `false` is returned, the special
     * attack is considered canceled and **no energy will be deducted**.
     *
     * @return `true` if the special attack was successfully activated and energy should be
     *   deducted; `false` to cancel the special attack and skip energy deduction.
     */
    public suspend fun ProtectedAccess.activate(): Boolean
}
