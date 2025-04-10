package org.rsmod.api.account.saver.request

import org.rsmod.game.entity.Player

public sealed class AccountSaveResponse {
    public abstract val player: Player

    public data class Success(override val player: Player) : AccountSaveResponse()

    public sealed class Failure : AccountSaveResponse()

    /**
     * Indicates a save request that failed to complete after the maximum number of retry attempts.
     *
     * The callback should handle this appropriately - such as by saving a local backup that can be
     * used to recover data later.
     */
    public data class ExcessiveRetries(override val player: Player) : Failure()

    /**
     * Indicates a save request that failed during the emergency shutdown failsafe due to an
     * internal server error.
     *
     * This should be handled similarly to [ExcessiveRetries], but may warrant higher-priority
     * actions such as notifying the operator or writing critical diagnostics.
     */
    public data class InternalShutdownError(override val player: Player) : Failure()
}
