package org.rsmod.api.player.output

import net.rsprot.protocol.game.outgoing.logout.Logout
import net.rsprot.protocol.game.outgoing.misc.client.ServerTickEnd
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
}
