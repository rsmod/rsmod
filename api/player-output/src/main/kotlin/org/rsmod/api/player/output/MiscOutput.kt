package org.rsmod.api.player.output

import net.rsprot.protocol.game.outgoing.logout.Logout
import net.rsprot.protocol.game.outgoing.logout.LogoutWithReason
import net.rsprot.protocol.game.outgoing.misc.client.ServerTickEnd
import net.rsprot.protocol.game.outgoing.misc.client.UpdateRebootTimer
import net.rsprot.protocol.game.outgoing.misc.player.SetPlayerOp
import org.rsmod.game.entity.Player

public object MiscOutput {
    /** @see [SetPlayerOp] */
    public fun setPlayerOp(player: Player, slot: Int, op: String?, priority: Boolean = false) {
        player.client.write(SetPlayerOp(slot, priority, op))
    }

    /** @see [ServerTickEnd] */
    public fun serverTickEnd(player: Player) {
        player.client.write(ServerTickEnd)
    }

    /** @see [Logout] */
    public fun logout(player: Player) {
        player.client.write(Logout)
    }

    /** Calls [LogoutWithReason] with an arg of `1` (reason = `Kicked`). */
    public fun logoutKicked(player: Player) {
        player.client.write(LogoutWithReason(reason = 1))
    }

    /** Calls [LogoutWithReason] with an arg of `2` (reason = `Updating`). */
    public fun logoutUpdating(player: Player) {
        player.client.write(LogoutWithReason(reason = 2))
    }

    /** @see [UpdateRebootTimer] */
    public fun updateRebootTimer(player: Player, cycles: Int) {
        require(cycles in 0..65535) { "`cycles` must be within range [0..65535]. (cycles=$cycles)" }
        player.client.write(UpdateRebootTimer(cycles))
    }

    /** Calls [UpdateRebootTimer] with an arg of `0`. */
    public fun clearUpdateRebootTimer(player: Player) {
        updateRebootTimer(player, cycles = 0)
    }
}
