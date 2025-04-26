package org.rsmod.api.player.output

import net.rsprot.protocol.game.outgoing.logout.Logout
import net.rsprot.protocol.game.outgoing.misc.client.ServerTickEnd
import net.rsprot.protocol.game.outgoing.misc.client.UpdateRebootTimer
import net.rsprot.protocol.game.outgoing.misc.player.SetPlayerOp
import org.rsmod.game.entity.Player

public object MiscOutput {
    public fun setPlayerOp(player: Player, slot: Int, op: String?, priority: Boolean = false) {
        player.client.write(SetPlayerOp(slot, priority, op))
    }

    public fun serverTickEnd(player: Player) {
        player.client.write(ServerTickEnd)
    }

    public fun logout(player: Player) {
        player.client.write(Logout)
    }

    public fun updateRebootTimer(player: Player, cycles: Int) {
        require(cycles in 0..65535) { "`cycles` must be within range [0..65535]. (cycles=$cycles)" }
        player.client.write(UpdateRebootTimer(cycles))
    }

    public fun clearUpdateRebootTimer(player: Player) {
        updateRebootTimer(player, cycles = 0)
    }
}
