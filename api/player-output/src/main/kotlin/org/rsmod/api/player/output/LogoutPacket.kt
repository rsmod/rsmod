package org.rsmod.api.player.output

import net.rsprot.protocol.game.outgoing.logout.Logout
import org.rsmod.game.entity.Player

public object LogoutPacket {
    public fun logout(player: Player) {
        player.client.write(Logout)
    }
}
