package org.rsmod.api.player.output

import net.rsprot.protocol.game.outgoing.misc.player.SetPlayerOp
import org.rsmod.game.entity.Player

public object MiscOutput {
    public fun setPlayerOp(player: Player, slot: Int, op: String?, priority: Boolean = false) {
        player.client.write(SetPlayerOp(slot, priority, op))
    }
}
