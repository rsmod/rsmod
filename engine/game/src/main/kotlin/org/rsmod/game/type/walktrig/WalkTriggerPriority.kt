package org.rsmod.game.type.walktrig

/**
 * Represents the priority of a walk trigger, determining whether it can be overwritten by another
 * trigger.
 *
 * Walk triggers are used in a variety of cases, ranging from minor effects to critical mechanics.
 * For example:
 * - Emotes use a walk trigger to reset animations upon movement.
 * - Freezing effects use a walk trigger to prevent movement entirely.
 *
 * _**Note:** This concept does not exist in the original game. It has been introduced here as a
 * safeguard to ensure proper behavior and prevent unintended bugs._
 */
public enum class WalkTriggerPriority(public val id: Int) {
    /** This walk trigger has no restrictions and can be overwritten by any other trigger. */
    None(0),
    /** This walk trigger can only be overwritten by other [Low] or [High] priority triggers. */
    Low(1),
    /** This walk trigger **cannot** be overwritten, even by another [High] priority trigger. */
    High(2);

    public fun canOverwrite(other: WalkTriggerPriority?): Boolean =
        when (other) {
            null -> true
            High -> false
            Low -> this == Low || this == High
            None -> true
        }

    public companion object {
        public operator fun get(id: Int): WalkTriggerPriority? =
            when (id) {
                None.id -> None
                Low.id -> Low
                High.id -> High
                else -> null
            }
    }
}
