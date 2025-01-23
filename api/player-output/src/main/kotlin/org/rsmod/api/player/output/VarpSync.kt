package org.rsmod.api.player.output

import net.rsprot.protocol.game.outgoing.varp.VarpLarge
import net.rsprot.protocol.game.outgoing.varp.VarpSmall
import org.rsmod.game.client.Client
import org.rsmod.game.entity.Player
import org.rsmod.game.type.varp.VarpType

public object VarpSync {
    /**
     * Calling this function directly will bypass [VarpType.transmit] and [VarpType.protect] flags.
     */
    public fun writeVarp(player: Player, varp: VarpType, value: Int) {
        writeVarp(player.client, varp, value)
    }

    /**
     * Calling this function directly will bypass [VarpType.transmit] and [VarpType.protect] flags.
     */
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
