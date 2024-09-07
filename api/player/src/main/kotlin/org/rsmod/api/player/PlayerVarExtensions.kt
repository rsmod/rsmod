package org.rsmod.api.player

import org.rsmod.game.entity.Player
import org.rsmod.game.type.varp.VarpType

public fun Player.forceDisconnect() {
    // TODO: disconnect player
    mes("TODO: Get Disconnected!")
}

public fun Player.syncVarp(varp: VarpType, value: Int) {
    vars[varp] = if (value == 0) null else value
    if (varp.canTransmit) {
        writeVarp(varp, value)
    }
}

public fun Player.syncVarpStr(varp: VarpType, value: String?) {
    // NOTE: Might be worth considering blank `value`s as null here to remove from var map.
    varsString[varp] = value
}
