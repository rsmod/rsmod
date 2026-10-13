package org.runerealm.api.player.output

import net.rsprot.protocol.game.outgoing.varp.VarpLarge
import net.rsprot.protocol.game.outgoing.varp.VarpSmall
import org.runerealm.game.client.Client
import org.runerealm.game.entity.Player
import org.runerealm.game.type.varp.VarpType

public object VarpSync {
    /** Calling this function directly will bypass [VarpType.transmit] conditions. */
    public fun writeVarp(player: Player, varp: VarpType, value: Int) {
        writeVarp(player.client, varp, value)
    }

    /** Calling this function directly will bypass [VarpType.transmit] conditions. */
    public fun writeVarp(client: Client<Any, Any>, varp: VarpType, value: Int) {
        val message =
            if (value in Byte.MIN_VALUE..Byte.MAX_VALUE) {
                VarpSmall(varp.id, value)
            } else {
                VarpLarge(varp.id, value)
            }
        client.write(message)
    }
}
